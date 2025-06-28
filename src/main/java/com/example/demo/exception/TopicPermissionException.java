// TopicPermissionException.java - 选题权限异常
package com.example.demo.exception;

public class TopicPermissionException extends RuntimeException {
    public TopicPermissionException(String message) {
        super(message);
    }

    public TopicPermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}