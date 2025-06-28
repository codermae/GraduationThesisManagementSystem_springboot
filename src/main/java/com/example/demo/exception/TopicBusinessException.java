// TopicBusinessException.java - 选题业务异常
package com.example.demo.exception;

public class TopicBusinessException extends RuntimeException {
    public TopicBusinessException(String message) {
        super(message);
    }

    public TopicBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}