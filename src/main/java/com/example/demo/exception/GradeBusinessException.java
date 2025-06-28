// GradeBusinessException.java
package com.example.demo.exception;

public class GradeBusinessException extends RuntimeException {
    public GradeBusinessException(String message) {
        super(message);
    }

    public GradeBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}