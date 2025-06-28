// GradeExceptionHandler.java
package com.example.demo.exception;

import com.example.demo.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GradeExceptionHandler {

    @ExceptionHandler(GradeBusinessException.class)
    public Result<Void> handleGradeBusinessException(GradeBusinessException e) {
        log.warn("成绩业务异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }
}
