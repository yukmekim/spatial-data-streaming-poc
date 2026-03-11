package dev.yukmekim.spatialdatastreamingpoc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yukmekim.spatialdatastreamingpoc.common.exception.BusinessException;
import dev.yukmekim.spatialdatastreamingpoc.common.exception.ErrorCode;
import dev.yukmekim.spatialdatastreamingpoc.dto.request.AppleItemRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanFileWriteService {

    private final ObjectMapper objectMapper;

    public void writeOrAppend(String baseDirPath, String fileName, List<AppleItemRequestDto> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        Path dirPath = Path.of(baseDirPath);
        Path filePath = dirPath.resolve(fileName);

        try {
            Files.createDirectories(dirPath);

            // 1. 파일이 없으면 새 파일로 전체 List 직렬화(생성)
            if (!Files.exists(filePath)) {
                objectMapper.writeValue(filePath.toFile(), items);
                return;
            }

            // 2. 파일이 이미 존재하면 RandomAccessFile을 통해 파일 끝에 텍스트 레벨에서 Append
            // 새 데이터 목록의 JSON 문자열 추출 (ex: "[{...}, {...}]")
            String newItemsJson = objectMapper.writeValueAsString(items);
            
            // 앞의 대괄호 '[' 는 제거 -> "{...}, {...}]"
            String appendContent = newItemsJson.substring(1);

            try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
                long length = raf.length();
                if (length < 2) {
                    // 비정상 파일(비어있거나 '[', ']' 만 있는 경우)은 덮어쓰기 폴백
                    objectMapper.writeValue(filePath.toFile(), items);
                    return;
                }

                // 커서를 파일 맨 끝의 전(마지막 문자열 ']') 위치로 이동
                raf.seek(length - 1);
                
                // 기존 데이터에 이어붙이므로 콤마(,) 삽입 후 나머지 JSON 삽입
                raf.write(",".getBytes(StandardCharsets.UTF_8));
                raf.write(appendContent.getBytes(StandardCharsets.UTF_8));
            }

        } catch (IOException e) {
            log.error("파일 저장/Append 실패 - 경로: {}", filePath, e);
            throw new BusinessException(ErrorCode.FILE_IO_ERROR);
        }
    }
}
