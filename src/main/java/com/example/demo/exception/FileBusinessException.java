// FileBusinessException.java - 文件业务异常
package com.example.demo.exception;

public class FileBusinessException extends RuntimeException {

    private final int code;

    public FileBusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public FileBusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public int getCode() {
        return code;
    }
}