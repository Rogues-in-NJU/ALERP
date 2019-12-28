package edu.nju.alerp.common;

import edu.nju.alerp.enums.ExceptionEnum;
import lombok.Data;

/**
 * 异常类，用于监测业务异常
 *
 * @author luhailong
 * @date 2019/12/28
 */
@Data
public class NJUException extends RuntimeException {
    private ExceptionEnum exceptionEnum;

    public NJUException(ExceptionEnum exceptionEnum) {
        this.exceptionEnum = exceptionEnum;
    }

    public NJUException(ExceptionEnum exceptionEnum,String message) {
        super(message);
        this.exceptionEnum = exceptionEnum;
    }

    public NJUException(String message, Throwable cause, ExceptionEnum exceptionEnum) {
        super(message, cause);
        this.exceptionEnum = exceptionEnum;
    }

    public NJUException(Throwable cause, ExceptionEnum exceptionEnum) {
        super(cause);
        this.exceptionEnum = exceptionEnum;
    }

    public NJUException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
        ExceptionEnum exceptionEnum) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.exceptionEnum = exceptionEnum;
    }
}
