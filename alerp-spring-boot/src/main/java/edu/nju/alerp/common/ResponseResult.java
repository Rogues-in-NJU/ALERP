package edu.nju.alerp.common;

import edu.nju.alerp.enums.ExceptionEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @Description: 回参
 * @Author: qianen.yin
 * @CreateDate: 2019-12-15 23:55
 */
@Data
@Builder
public class ResponseResult<T> {

    private String message;

    private int code;

    private T data;

    public static ResponseResult<String> ok() {
        return ok("success", ExceptionEnum.SUCCESS);
    }

    public static <T> ResponseResult<T> ok(T data) {
        return ok(data, ExceptionEnum.SUCCESS);
    }

    public static <T> ResponseResult<T> ok(T data, ExceptionEnum exceptionEnum) {
        return ResponseResult
                .<T>builder()
                .code(exceptionEnum.getCode())
                .message(exceptionEnum.getMessage())
                .data(data)
                .build();
    }

    public static <T> ResponseResult<T> fail(ExceptionEnum exceptionEnum,
                                             String message) {
        return ResponseResult
                .<T>builder()
                .code(exceptionEnum.getCode())
                .message(String.format("[%s]%s", exceptionEnum.getMessage(), message))
                .build();
    }

    public static <T> ResponseResult<T> fail(ExceptionWrapper exceptionWrapper) {
        return ResponseResult
                .<T>builder()
                .code(exceptionWrapper.getExceptionEnum().getCode())
                .message(exceptionWrapper.getErrorMessage())
                .build();
    }
}

