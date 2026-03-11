package dev.yukmekim.spatialdatastreamingpoc.controller;

import dev.yukmekim.spatialdatastreamingpoc.common.response.Response;
import dev.yukmekim.spatialdatastreamingpoc.dto.request.ScanDataRequestDto;
import dev.yukmekim.spatialdatastreamingpoc.dto.response.ScanProcessResponseDto;
import dev.yukmekim.spatialdatastreamingpoc.service.ScanService;
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

    private final ScanService scanService;

    @Operation(summary = "스캔 데이터 수신", description = "ROS로부터 과실 스캔 데이터를 수신하여 40x40 구역으로 분할 저장합니다.")
    @PostMapping
    public ResponseEntity<Response<ScanProcessResponseDto>> receiveScanData(
            @Valid @RequestBody ScanDataRequestDto request) {

        ScanProcessResponseDto result = scanService.processScanData(request);
        return ResponseEntity.ok(Response.success(result));
    }
}
