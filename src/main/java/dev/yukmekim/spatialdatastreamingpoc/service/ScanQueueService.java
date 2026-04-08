package dev.yukmekim.spatialdatastreamingpoc.service;

import dev.yukmekim.spatialdatastreamingpoc.common.exception.BusinessException;
import dev.yukmekim.spatialdatastreamingpoc.common.exception.ErrorCode;
import dev.yukmekim.spatialdatastreamingpoc.config.ScanProperties;
import dev.yukmekim.spatialdatastreamingpoc.dto.request.AppleItemRequestDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
public class ScanQueueService {

    private final ScanService scanService;
    private final BlockingQueue<QueuedScanRequest> scanDataQueue;

    public record QueuedScanRequest(String versionCode, List<AppleItemRequestDto> items) {}

    public ScanQueueService(ScanService scanService, ScanProperties scanProperties) {
        this.scanService = scanService;
        this.scanDataQueue = new LinkedBlockingQueue<>(scanProperties.queueCapacity());
    }

    /**
     * 외부 API로부터 요청된 데이터를 큐에 적재합니다. (비동기 응답용)
     */
    public void enqueue(String versionCode, List<AppleItemRequestDto> items) {
        QueuedScanRequest request = new QueuedScanRequest(versionCode, items);
        if (!scanDataQueue.offer(request)) {
            log.error("Scan Data Queue is Full! 수신된 데이터를 버립니다. 버전: {}", versionCode);
            throw new BusinessException(ErrorCode.QUEUE_FULL);
        }
        log.info("스캔 데이터 큐 적재 성공 - 현재 대기열: {}개", scanDataQueue.size());
    }

    /**
     * 별도의 백그라운드 스레드 한 개만 가동하여 순차적으로 큐에서 꺼내어 파일 쓰기를 수행합니다.
     * 이 단일 스레드는 Race Condition 충돌을 원천 차단합니다.
     */
    @PostConstruct
    public void initQueueWorker() {
        Thread worker = new Thread(() -> {
            log.info("스캔 데이터 순차 처리(Queue Worker) 백그라운드 스레드가 시작되었습니다.");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 큐에 데이터가 들어올 때까지 대기(Blocked)
                    QueuedScanRequest request = scanDataQueue.take();
                    log.info("대기열에서 스캔 데이터 추출. 처리를 시작합니다. 남은 대기열: {}", scanDataQueue.size());
                    
                    // 기존 동기식 파티셔닝 빛 파일 쓰기 로직 실행
                    scanService.processScanData(request.versionCode(), request.items());
                    
                } catch (InterruptedException e) {
                    log.warn("Queue Worker 스레드가 인터럽트 되었습니다. 종료합니다.");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    // 데이터 처리 중 예외가 발생하더라도 스레드가 죽지 않도록 방어
                    log.error("비동기 데이터 저장 처리 중 에러 발생: ", e);
                }
            }
        });
        
        worker.setName("scan-queue-worker");
        worker.setDaemon(true); // 스프링 부트 종료시 안전하게 같이 종료되도록 데몬 스레드로 설정
        worker.start();
    }
}
