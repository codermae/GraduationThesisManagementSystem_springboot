// FileExceptionHandler.java - 文件异常处理器
package com.example.demo.exception;

import com.example.demo.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class FileExceptionHandler {

    @ExceptionHandler(FileBusinessException.class)
    public Result<Void> handleFileBusinessException(FileBusinessException e) {
        log.error("文件业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("文件大小超过限制: {}", e.getMessage());
        return Result.error(400, "文件大小超过限制，请上传小于50MB的文件");
    }
}