package dev.yukmekim.spatialdatastreamingpoc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yukmekim.spatialdatastreamingpoc.common.exception.BusinessException;
import dev.yukmekim.spatialdatastreamingpoc.common.exception.ErrorCode;
import dev.yukmekim.spatialdatastreamingpoc.dto.request.AppleItemRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanFileWriteService {

    private final ObjectMapper objectMapper;

    public void writeOrAppend(String baseDirPath, String fileName, List<AppleItemRequestDto> items) {
        Path dirPath = Path.of(baseDirPath);
        Path filePath = dirPath.resolve(fileName);

        try {
            Files.createDirectories(dirPath);

            List<AppleItemRequestDto> merged = new ArrayList<>();
            if (Files.exists(filePath)) {
                List<AppleItemRequestDto> existing = objectMapper.readValue(
                        filePath.toFile(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, AppleItemRequestDto.class)
                );
                merged.addAll(existing);
            }
            merged.addAll(items);
            objectMapper.writeValue(filePath.toFile(), merged);

        } catch (IOException e) {
            log.error("파일 저장 실패 - 경로: {}", filePath, e);
            throw new BusinessException(ErrorCode.FILE_IO_ERROR);
        }
    }
}
