package dev.yukmekim.spatialdatastreamingpoc.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "유효하지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "허용되지 않은 HTTP 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "엔티티를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 오류가 발생했습니다."),

    // File
    FILE_IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F001", "파일 저장 중 오류가 발생했습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "F002", "해당 파일(파티셔닝 완료된 JSON)을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
