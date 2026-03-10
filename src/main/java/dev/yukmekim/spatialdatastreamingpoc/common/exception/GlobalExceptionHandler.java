package dev.yukmekim.spatialdatastreamingpoc.common.exception;

import dev.yukmekim.spatialdatastreamingpoc.common.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<Response<Void>> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(Response.fail(errorCode.getCode(), e.getMessage()));
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<Response<Void>> handleBindException(BindException e) {
        log.warn("BindException: {}", e.getMessage());
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .badRequest()
                .body(Response.fail(ErrorCode.INVALID_INPUT_VALUE.getCode(), message));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<Response<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        log.warn("MethodNotAllowed: {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.METHOD_NOT_ALLOWED.getStatus())
                .body(Response.fail(ErrorCode.METHOD_NOT_ALLOWED.getCode(), ErrorCode.METHOD_NOT_ALLOWED.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Response<Void>> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);
        return ResponseEntity
                .internalServerError()
                .body(Response.fail(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
