package dev.yukmekim.spatialdatastreamingpoc.dto.response;

import dev.yukmekim.spatialdatastreamingpoc.domain.ScanAreaDataInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScanAreaResponseDto {

    private Float startX;
    private Float endX;
    private Float startZ;
    private Float endZ;
    private String fileName;
    private int cntGrade0;
    private int cntGrade1;
    private int cntGrade2;
    private int cntGrade3;
    private int totalCount;

    public static ScanAreaResponseDto from(ScanAreaDataInfo entity) {
        return ScanAreaResponseDto.builder()
                .startX(entity.getStartX())
                .endX(entity.getEndX())
                .startZ(entity.getStartZ())
                .endZ(entity.getEndZ())
                .fileName(entity.getFileName())
                .cntGrade0(entity.getCntGrade0())
                .cntGrade1(entity.getCntGrade1())
                .cntGrade2(entity.getCntGrade2())
                .cntGrade3(entity.getCntGrade3())
                .totalCount(entity.getCntGrade0() + entity.getCntGrade1() + 
                            entity.getCntGrade2() + entity.getCntGrade3())
                .build();
    }
}
