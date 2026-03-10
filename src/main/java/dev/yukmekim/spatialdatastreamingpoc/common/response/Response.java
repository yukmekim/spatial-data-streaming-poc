package dev.yukmekim.spatialdatastreamingpoc.common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Response<T> {

    private boolean success;
    private T data;
    private String code;
    private String message;

    public static <T> Response<T> success(T data) {
        return new Response<>(true, data, "200", "성공");
    }

    public static <T> Response<T> success(T data, String message) {
        return new Response<>(true, data, "200", message);
    }

    public static Response<Void> fail(String code, String message) {
        return new Response<>(false, null, code, message);
    }
}
