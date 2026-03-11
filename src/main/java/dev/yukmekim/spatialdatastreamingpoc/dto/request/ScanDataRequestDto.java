package dev.yukmekim.spatialdatastreamingpoc.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 100000, message = "데이터는 1회 최대 10만 건까지만 전송 가능합니다. 10만 건을 초과할 경우 분할하여 전송해주세요.")
    private List<AppleItemRequestDto> data;
}
