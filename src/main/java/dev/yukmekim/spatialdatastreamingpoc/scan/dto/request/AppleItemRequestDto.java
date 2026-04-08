package dev.yukmekim.spatialdatastreamingpoc.scan.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppleItemRequestDto {

    @Valid
    @NotNull
    private PoseDto pose;

    @Valid
    @NotNull
    private GradeDto grade;
}
