package dev.yukmekim.spatialdatastreamingpoc.service;

import dev.yukmekim.spatialdatastreamingpoc.dto.request.ScanDataRequestDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanQueueService {

    private final ScanService scanService;

    // 인메모리 큐: 최대 100개의 분할 패킷(2만건씩 100번 = 200만건어치) 수용 가능
    private final BlockingQueue<ScanDataRequestDto> scanDataQueue = new LinkedBlockingQueue<>(100);

    /**
     * 외부 API로부터 요청된 데이터를 큐에 적재합니다. (비동기 응답용)
     */
    public void enqueue(ScanDataRequestDto request) {
        if (!scanDataQueue.offer(request)) {
            log.error("Scan Data Queue is Full! 수신된 데이터를 버립니다. 버전: {}", request.getVersionCode());
            // 필요에 따라 Queue Full Exception 처리 가능 (현재는 로깅만)
            throw new RuntimeException("서버 데이터 처리량이 포화상태입니다. 잠시 후 묶음(Chunk)부터 재시도 해주세요.");
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
                    ScanDataRequestDto request = scanDataQueue.take();
                    log.info("대기열에서 스캔 데이터 추출. 처리를 시작합니다. 남은 대기열: {}", scanDataQueue.size());
                    
                    // 기존 동기식 파티셔닝 빛 파일 쓰기 로직 실행
                    scanService.processScanData(request);
                    
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
