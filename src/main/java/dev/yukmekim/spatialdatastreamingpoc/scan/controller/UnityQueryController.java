package dev.yukmekim.spatialdatastreamingpoc.scan.controller;

import dev.yukmekim.spatialdatastreamingpoc.common.response.Response;
import dev.yukmekim.spatialdatastreamingpoc.scan.dto.response.ScanAreaResponseDto;
import dev.yukmekim.spatialdatastreamingpoc.scan.service.ScanQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Unity", description = "Unity 대상 스캔 데이터 전달 API")
@RestController
@RequestMapping("/api/v1/unity/scan")
@RequiredArgsConstructor
public class UnityQueryController {

    private final ScanQueryService scanQueryService;

    @Operation(summary = "버전별 전체 구역 목록 조회", description = "Unity가 해당 버전의 분할된 구역(Area) 메타 정보와 다운로드 파일명 목록을 조회합니다.")
    @GetMapping("/{versionCode}/areas")
    public ResponseEntity<Response<List<ScanAreaResponseDto>>> getScanAreas(
            @PathVariable("versionCode") String versionCode) {
        
        List<ScanAreaResponseDto> areas = scanQueryService.getAreas(versionCode);
        return ResponseEntity.ok(Response.success(areas));
    }

    @Operation(summary = "단일 구역 스캔 데이터 다운로드", description = "Unity가 특정 Area 파일명으로 원본 JSON 데이터를 스트리밍 받아 로드합니다.")
    @GetMapping(value = "/{versionCode}/data/{fileName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> getScanAreaData(
            @PathVariable("versionCode") String versionCode,
            @PathVariable("fileName") String fileName) {
        
        Resource resource = scanQueryService.getAreaDataFile(versionCode, fileName);
        
        return ResponseEntity.ok()
                // attachment 대신 inline으로 브라우저/클라이언트(유니티) 가 직접 내용을 소화할 수 있도록 처리
                // 한글 깨짐 방지를 위해 UTF-8 명시
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
