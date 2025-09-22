package com.example.socialmeet.exception;

/**
 * 动态业务异常
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
public class DynamicBusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private String errorCode;
    
    public DynamicBusinessException(String message) {
        super(message);
    }
    
    public DynamicBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DynamicBusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public DynamicBusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
