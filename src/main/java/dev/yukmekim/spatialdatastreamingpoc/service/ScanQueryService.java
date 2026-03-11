package dev.yukmekim.spatialdatastreamingpoc.service;

import dev.yukmekim.spatialdatastreamingpoc.common.exception.BusinessException;
import dev.yukmekim.spatialdatastreamingpoc.common.exception.ErrorCode;
import dev.yukmekim.spatialdatastreamingpoc.domain.ScanFileInfo;
import dev.yukmekim.spatialdatastreamingpoc.dto.response.ScanAreaResponseDto;
import dev.yukmekim.spatialdatastreamingpoc.repository.ScanAreaDataInfoRepository;
import dev.yukmekim.spatialdatastreamingpoc.repository.ScanFileInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        Path filePath = Paths.get(fileInfo.getBaseDirPath(), fileName);
        
        if (!Files.exists(filePath)) {
            log.error("요청한 파일이 존재하지 않습니다: {}", filePath.toAbsolutePath());
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }

        return new FileSystemResource(filePath.toFile());
    }
}
