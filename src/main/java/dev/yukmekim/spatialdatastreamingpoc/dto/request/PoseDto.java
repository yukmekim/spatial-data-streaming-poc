package dev.yukmekim.spatialdatastreamingpoc.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PoseDto {

    @NotNull
    private Float tx;

    @NotNull
    private Float ty;

    @NotNull
    private Float tz;

    private Float qw;
    private Float qx;
    private Float qy;
    private Float qz;
}
