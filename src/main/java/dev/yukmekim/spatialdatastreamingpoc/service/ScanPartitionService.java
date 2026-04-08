package dev.yukmekim.spatialdatastreamingpoc.service;

import dev.yukmekim.spatialdatastreamingpoc.config.ScanProperties;
import dev.yukmekim.spatialdatastreamingpoc.dto.request.AppleItemRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScanPartitionService {

    private final ScanProperties scanProperties;

    public GridCell resolveCell(float tx, float tz) {
        ScanProperties.Partition p = scanProperties.partition();
        int xIdx = clamp((int) Math.floor((tx - p.xMin()) / p.step()));
        int zIdx = clamp((int) Math.floor((tz - p.zMin()) / p.step()));

        float startX = roundCoord(p.xMin() + xIdx * p.step());
        float startZ = roundCoord(p.zMin() + zIdx * p.step());

        return new GridCell(startX, roundCoord(startX + p.step()), startZ, roundCoord(startZ + p.step()));
    }

    public String generateFileName(GridCell cell) {
        return String.format("area_%s_%s_%s_%s.json",
                formatCoord(cell.startX()), formatCoord(cell.endX()),
                formatCoord(cell.startZ()), formatCoord(cell.endZ()));
    }

    public Map<GridCell, List<AppleItemRequestDto>> groupByCell(List<AppleItemRequestDto> data) {
        return data.stream()
                .collect(Collectors.groupingBy(
                        item -> resolveCell(item.getPose().getTx().floatValue(), item.getPose().getTz().floatValue())
                ));
    }

    private int clamp(int idx) {
        return Math.max(0, Math.min(scanProperties.partition().gridCount() - 1, idx));
    }

    private float roundCoord(float value) {
        return Math.round(value * 100) / 100.0f;
    }

    public String generateKey(float startX, float startZ) {
        return formatCoord(startX) + "_" + formatCoord(startZ);
    }

    private String formatCoord(float value) {
        BigDecimal bd = BigDecimal.valueOf(Math.round(value * 100))
                .divide(BigDecimal.valueOf(100))
                .stripTrailingZeros();
        String plain = bd.toPlainString();
        return plain.contains(".") ? plain : plain + ".0";
    }
}
