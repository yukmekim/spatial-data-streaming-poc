package dev.yukmekim.spatialdatastreamingpoc.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GradeDto {

    @NotNull
    private Integer g;
}
