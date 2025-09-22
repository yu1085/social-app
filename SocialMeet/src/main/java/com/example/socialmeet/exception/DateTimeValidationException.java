package com.example.socialmeet.exception;

/**
 * 日期时间验证异常
 * 当日期时间字段验证失败时抛出
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
public class DateTimeValidationException extends RuntimeException {
    
    public DateTimeValidationException(String message) {
        super(message);
    }
    
    public DateTimeValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
