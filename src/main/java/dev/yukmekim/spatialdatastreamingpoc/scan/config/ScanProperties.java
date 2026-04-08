package dev.yukmekim.spatialdatastreamingpoc.scan.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "scan")
public record ScanProperties(
        @NotBlank String baseDir,
        @Positive int queueCapacity,
        @Valid @NotNull Partition partition
) {
    public record Partition(
            @NotNull Float xMin,
            @NotNull Float zMin,
            @Positive float step,
            @Positive int gridCount
    ) {}
}
