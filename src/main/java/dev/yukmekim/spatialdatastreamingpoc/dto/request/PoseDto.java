package dev.yukmekim.spatialdatastreamingpoc.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Double qw;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Double qx;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Double qy;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Double qz;
}
