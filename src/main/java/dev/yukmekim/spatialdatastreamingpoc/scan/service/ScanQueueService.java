package dev.yukmekim.spatialdatastreamingpoc.scan.service;

import dev.yukmekim.spatialdatastreamingpoc.common.exception.BusinessException;
import dev.yukmekim.spatialdatastreamingpoc.common.exception.ErrorCode;
import dev.yukmekim.spatialdatastreamingpoc.scan.config.ScanProperties;
import dev.yukmekim.spatialdatastreamingpoc.scan.dto.request.AppleItemRequestDto;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
public class ScanQueueService {

    private final ScanService scanService;
    private final BlockingQueue<QueuedScanRequest> scanDataQueue;
    private final ThreadPoolTaskExecutor executor;

    public record QueuedScanRequest(String versionCode, List<AppleItemRequestDto> items) {}

    public ScanQueueService(ScanService scanService, ScanProperties scanProperties) {
        this.scanService = scanService;
        this.scanDataQueue = new LinkedBlockingQueue<>(scanProperties.queueCapacity());
        this.executor = createExecutor();
        this.executor.execute(this::processQueue);
    }

    public void enqueue(String versionCode, List<AppleItemRequestDto> items) {
        QueuedScanRequest request = new QueuedScanRequest(versionCode, items);
        if (!scanDataQueue.offer(request)) {
            log.error("Scan Data Queue is Full! 수신된 데이터를 버립니다. 버전: {}", versionCode);
            throw new BusinessException(ErrorCode.QUEUE_FULL);
        }
        log.info("스캔 데이터 큐 적재 성공 - 현재 대기열: {}개", scanDataQueue.size());
    }

    private void processQueue() {
        log.info("스캔 데이터 순차 처리(Queue Worker) 백그라운드 스레드가 시작되었습니다.");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                QueuedScanRequest request = scanDataQueue.take();
                log.info("대기열에서 스캔 데이터 추출. 처리를 시작합니다. 남은 대기열: {}", scanDataQueue.size());
                scanService.processScanData(request.versionCode(), request.items());
            } catch (InterruptedException e) {
                log.warn("Queue Worker 스레드가 인터럽트 되었습니다. 종료합니다.");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("비동기 데이터 저장 처리 중 에러 발생: ", e);
            }
        }
    }

    private ThreadPoolTaskExecutor createExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setMaxPoolSize(1);
        taskExecutor.setThreadNamePrefix("scan-queue-worker-");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(30);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @PreDestroy
    public void shutdown() {
        log.info("Queue Worker 종료를 시작합니다. 남은 대기열: {}개", scanDataQueue.size());
        executor.shutdown();
    }
}
