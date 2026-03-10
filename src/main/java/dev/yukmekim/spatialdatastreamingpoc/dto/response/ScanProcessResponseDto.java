package dev.yukmekim.spatialdatastreamingpoc.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScanProcessResponseDto {

    private String versionCode;
    private int receivedCount;
    private int upsertedAreaCount;
}
