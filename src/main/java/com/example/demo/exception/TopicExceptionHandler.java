// TopicExceptionHandler.java - 选题模块异常处理
package com.example.demo.exception;

import com.example.demo.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TopicExceptionHandler {

    /**
     * 处理选题相关业务异常
     */
    @ExceptionHandler(TopicBusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleTopicBusinessException(TopicBusinessException e) {
        log.warn("选题业务异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 处理选题权限异常
     */
    @ExceptionHandler(TopicPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleTopicPermissionException(TopicPermissionException e) {
        log.warn("选题权限异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }
}