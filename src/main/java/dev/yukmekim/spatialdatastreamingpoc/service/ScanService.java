package dev.yukmekim.spatialdatastreamingpoc.service;

import dev.yukmekim.spatialdatastreamingpoc.domain.ScanAreaDataInfo;
import dev.yukmekim.spatialdatastreamingpoc.domain.ScanFileInfo;
import dev.yukmekim.spatialdatastreamingpoc.dto.request.AppleItemRequestDto;
import dev.yukmekim.spatialdatastreamingpoc.dto.request.ScanDataRequestDto;
import dev.yukmekim.spatialdatastreamingpoc.dto.response.ScanProcessResponseDto;
import dev.yukmekim.spatialdatastreamingpoc.repository.ScanAreaDataInfoRepository;
import dev.yukmekim.spatialdatastreamingpoc.repository.ScanFileInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ScanService {

    private final ScanFileInfoRepository scanFileInfoRepository;
    private final ScanAreaDataInfoRepository scanAreaDataInfoRepository;
    private final ScanPartitionService scanPartitionService;
    private final ScanFileWriteService scanFileWriteService;

    public ScanProcessResponseDto processScanData(ScanDataRequestDto request) {
        ScanFileInfo scanFileInfo = getOrCreateScanFileInfo(request);

        Map<GridCell, List<AppleItemRequestDto>> grouped =
                scanPartitionService.groupByCell(request.getData());

        List<ScanAreaDataInfo> areaDataList = buildAreaDataList(scanFileInfo, grouped);
        scanAreaDataInfoRepository.saveAll(areaDataList);

        log.info("스캔 데이터 처리 완료 - 버전: {}, 수신: {}건, 처리 구역: {}개",
                scanFileInfo.getVersionCode(), request.getData().size(), areaDataList.size());

        return ScanProcessResponseDto.builder()
                .versionCode(scanFileInfo.getVersionCode())
                .receivedCount(request.getData().size())
                .upsertedAreaCount(areaDataList.size())
                .build();
    }

    private ScanFileInfo getOrCreateScanFileInfo(ScanDataRequestDto request) {
        return scanFileInfoRepository.findByVersionCode(request.getVersionCode())
                .orElseGet(() -> scanFileInfoRepository.save(
                        ScanFileInfo.builder()
                                .versionCode(request.getVersionCode())
                                .baseDirPath(Path.of(request.getBaseDirPath(), request.getVersionCode()).toString())
                                .date(request.getScanDate())
                                .build()
                ));
    }

    private List<ScanAreaDataInfo> buildAreaDataList(
            ScanFileInfo scanFileInfo,
            Map<GridCell, List<AppleItemRequestDto>> grouped) {

        List<ScanAreaDataInfo> result = new ArrayList<>();
        
        // N+1 문제 해결: 해당 버전의 구역 정보를 한 번에 읽어와서 메모리에 Map으로 캐싱
        List<ScanAreaDataInfo> existingAreas = scanAreaDataInfoRepository.findAllByScanFileInfoIdWithFetch(scanFileInfo.getId());
        Map<String, ScanAreaDataInfo> areaMap = existingAreas.stream()
                .collect(Collectors.toMap(
                        a -> scanPartitionService.generateKey(a.getStartX(), a.getStartZ()),
                        a -> a
                ));

        for (Map.Entry<GridCell, List<AppleItemRequestDto>> entry : grouped.entrySet()) {
            GridCell cell = entry.getKey();
            List<AppleItemRequestDto> items = entry.getValue();
            String fileName = scanPartitionService.generateFileName(cell);
            String cellKey = scanPartitionService.generateKey(cell.startX(), cell.startZ());
            int[] counts = countByGrade(items);

            ScanAreaDataInfo areaData = areaMap.getOrDefault(cellKey, 
                    ScanAreaDataInfo.builder()
                            .scanFileInfo(scanFileInfo)
                            .startX(cell.startX())
                            .endX(cell.endX())
                            .startZ(cell.startZ())
                            .endZ(cell.endZ())
                            .cntGrade0(0)
                            .cntGrade1(0)
                            .cntGrade2(0)
                            .cntGrade3(0)
                            .fileName(fileName)
                            .build()
            );

            areaData.accumulateCounts(counts[0], counts[1], counts[2], counts[3]);
            scanFileWriteService.writeOrAppend(scanFileInfo.getBaseDirPath(), fileName, items);
            result.add(areaData);
        }

        return result;
    }

    private int[] countByGrade(List<AppleItemRequestDto> items) {
        int[] counts = new int[4];
        for (AppleItemRequestDto item : items) {
            int g = item.getGrade().getG();
            if (g >= 0 && g <= 3) counts[g]++;
        }
        return counts;
    }
}
