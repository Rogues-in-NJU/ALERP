package edu.nju.alerp.common;

import edu.nju.alerp.enums.ExceptionEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @Description: 错误包装类
 * @Author: qianen.yin
 * @CreateDate: 2019-12-15 23:53
 */
@Data
@Builder
public class ExceptionWrapper {

    private ExceptionEnum exceptionEnum;

    private String errorMessage;


    public static ExceptionWrapper defaultExceptionWrapper(Throwable t) {
        return ExceptionWrapper.builder()
                .exceptionEnum(ExceptionEnum.SERVER_ERROR)
                .errorMessage(
                        String.format("[%s]%s", ExceptionEnum.SERVER_ERROR.getMessage(),
                                t.getMessage()))
                .build();
    }
}
