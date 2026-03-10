package dev.yukmekim.spatialdatastreamingpoc.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class ScanDataRequestDto {

    @NotBlank(message = "버전 코드는 필수입니다.")
    private String versionCode;

    @NotBlank(message = "기본 디렉토리 경로는 필수입니다.")
    private String baseDirPath;

    @NotNull(message = "스캔 날짜는 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scanDate;

    @Valid
    @NotEmpty(message = "스캔 데이터는 1건 이상이어야 합니다.")
    private List<AppleItemRequestDto> data;
}
