package dev.yukmekim.spatialdatastreamingpoc.service;

import dev.yukmekim.spatialdatastreamingpoc.dto.request.AppleItemRequestDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ScanPartitionService {

    private static final float X_MIN = -5.0f;
    private static final float Z_MIN = -5.0f;
    private static final float STEP = 0.25f;
    private static final int GRID_COUNT = 40;

    public GridCell resolveCell(float tx, float tz) {
        int xIdx = clamp((int) Math.floor((tx - X_MIN) / STEP));
        int zIdx = clamp((int) Math.floor((tz - Z_MIN) / STEP));

        float startX = roundCoord(X_MIN + xIdx * STEP);
        float startZ = roundCoord(Z_MIN + zIdx * STEP);

        return new GridCell(startX, roundCoord(startX + STEP), startZ, roundCoord(startZ + STEP));
    }

    public String generateFileName(GridCell cell) {
        return String.format("area_%s_%s_%s_%s.json",
                formatCoord(cell.startX()), formatCoord(cell.endX()),
                formatCoord(cell.startZ()), formatCoord(cell.endZ()));
    }

    public Map<GridCell, List<AppleItemRequestDto>> groupByCell(List<AppleItemRequestDto> data) {
        return data.stream()
                .collect(Collectors.groupingBy(
                        item -> resolveCell(item.getPose().getTx(), item.getPose().getTz())
                ));
    }

    private int clamp(int idx) {
        return Math.max(0, Math.min(GRID_COUNT - 1, idx));
    }

    private float roundCoord(float value) {
        return Math.round(value * 100) / 100.0f;
    }

    private String formatCoord(float value) {
        BigDecimal bd = BigDecimal.valueOf(Math.round(value * 100))
                .divide(BigDecimal.valueOf(100))
                .stripTrailingZeros();
        String plain = bd.toPlainString();
        return plain.contains(".") ? plain : plain + ".0";
    }
}
