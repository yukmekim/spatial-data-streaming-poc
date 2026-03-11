package dev.yukmekim.spatialdatastreamingpoc.controller;

import dev.yukmekim.spatialdatastreamingpoc.common.response.Response;
import dev.yukmekim.spatialdatastreamingpoc.dto.request.ScanDataRequestDto;
import dev.yukmekim.spatialdatastreamingpoc.service.ScanQueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Scan", description = "ROS 스캔 데이터 수신 API")
@RestController
@RequestMapping("/api/v1/scan")
@RequiredArgsConstructor
public class ScanController {

    private final ScanQueueService scanQueueService;

    @Operation(summary = "스캔 데이터 수신 (비동기 순차 큐 봇)", description = "ROS 데이터를 큐에 넣고 즉시 응답합니다. 서버 내에서 스레드 1개가 순차 처리하므로 I/O 충돌을 방지합니다.")
    @PostMapping
    public ResponseEntity<Response<String>> receiveScanDataAsync(
            @Valid @RequestBody ScanDataRequestDto request) {

        scanQueueService.enqueue(request);
        return ResponseEntity.ok(Response.success("데이터가 성공적으로 수신 큐에 등록되었습니다. 백그라운드에서 순차 처리됩니다."));
    }
}
