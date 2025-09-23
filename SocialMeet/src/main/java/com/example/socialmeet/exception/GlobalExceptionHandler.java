package com.example.socialmeet.exception;

import com.example.socialmeet.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        System.err.println("=== 全局异常处理器捕获异常 ===");
        System.err.println("异常类型: " + e.getClass().getSimpleName());
        System.err.println("异常消息: " + e.getMessage());
        System.err.println("异常堆栈: ");
        e.printStackTrace();
        System.err.println("=====================================");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("系统异常: " + e.getMessage()));
    }
}
