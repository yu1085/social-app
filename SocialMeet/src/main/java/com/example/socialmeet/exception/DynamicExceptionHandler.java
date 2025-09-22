package com.example.socialmeet.exception;

import com.example.socialmeet.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态模块异常处理器
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestControllerAdvice
public class DynamicExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicExceptionHandler.class);
    
    /**
     * 处理动态相关业务异常
     */
    @ExceptionHandler(DynamicBusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleDynamicBusinessException(
            DynamicBusinessException ex, WebRequest request) {
        
        logger.warn("动态业务异常: {} - 请求: {}", ex.getMessage(), request.getDescription(false));
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        response.setTimestamp(LocalDateTime.now());
        response.setPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        logger.warn("参数验证异常: {} - 请求: {}", ex.getMessage(), request.getDescription(false));
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse<Map<String, String>> response = ApiResponse.error("参数验证失败");
        response.setData(errors);
        response.setTimestamp(LocalDateTime.now());
        response.setPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        logger.warn("非法参数异常: {} - 请求: {}", ex.getMessage(), request.getDescription(false));
        
        ApiResponse<Object> response = ApiResponse.error("参数错误: " + ex.getMessage());
        response.setTimestamp(LocalDateTime.now());
        response.setPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        logger.error("运行时异常: {} - 请求: {}", ex.getMessage(), request.getDescription(false), ex);
        
        ApiResponse<Object> response = ApiResponse.error("系统内部错误");
        response.setTimestamp(LocalDateTime.now());
        response.setPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.error("未知异常: {} - 请求: {}", ex.getMessage(), request.getDescription(false), ex);
        
        ApiResponse<Object> response = ApiResponse.error("系统异常，请联系管理员");
        response.setTimestamp(LocalDateTime.now());
        response.setPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
