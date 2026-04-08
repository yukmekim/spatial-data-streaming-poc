package dev.yukmekim.spatialdatastreamingpoc.scan.service;

import dev.yukmekim.spatialdatastreamingpoc.common.exception.BusinessException;
import dev.yukmekim.spatialdatastreamingpoc.common.exception.ErrorCode;
import dev.yukmekim.spatialdatastreamingpoc.scan.domain.ScanFileInfo;
import dev.yukmekim.spatialdatastreamingpoc.scan.dto.response.ScanAreaResponseDto;
import dev.yukmekim.spatialdatastreamingpoc.scan.repository.ScanAreaDataInfoRepository;
import dev.yukmekim.spatialdatastreamingpoc.scan.repository.ScanFileInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScanQueryService {

    private final ScanFileInfoRepository scanFileInfoRepository;
    private final ScanAreaDataInfoRepository scanAreaDataInfoRepository;

    public List<ScanAreaResponseDto> getAreas(String versionCode) {
        ScanFileInfo fileInfo = scanFileInfoRepository.findByVersionCode(versionCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        return scanAreaDataInfoRepository.findByScanFileInfo(fileInfo)
                .stream()
                .map(ScanAreaResponseDto::from)
                .collect(Collectors.toList());
    }

    public Resource getAreaDataFile(String versionCode, String fileName) {
        ScanFileInfo fileInfo = scanFileInfoRepository.findByVersionCode(versionCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        Path basePath = Path.of(fileInfo.getBaseDirPath()).normalize();
        Path filePath = basePath.resolve(fileName).normalize();

        if (!filePath.startsWith(basePath)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!Files.exists(filePath)) {
            log.error("요청한 파일이 존재하지 않습니다: {}", filePath.toAbsolutePath());
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }

        return new FileSystemResource(filePath.toFile());
    }
}
