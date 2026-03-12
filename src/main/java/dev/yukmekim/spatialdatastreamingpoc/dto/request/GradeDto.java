package dev.yukmekim.spatialdatastreamingpoc.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GradeDto {

    @NotNull
    @Min(value = 0, message = "숙도 등급은 0 이상이어야 합니다.")
    @Max(value = 3, message = "숙도 등급은 3 이하이어야 합니다.")
    private Integer g;
}
