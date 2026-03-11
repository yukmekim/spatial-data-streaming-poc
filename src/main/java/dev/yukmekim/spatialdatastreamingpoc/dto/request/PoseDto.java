package dev.yukmekim.spatialdatastreamingpoc.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PoseDto {

    @NotNull
    private Double tx;

    @NotNull
    private Double ty;

    @NotNull
    private Double tz;

    private Double qw;
    private Double qx;
    private Double qy;
    private Double qz;
}
