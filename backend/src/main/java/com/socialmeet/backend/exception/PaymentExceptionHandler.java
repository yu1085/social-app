package com.socialmeet.backend.exception;

import com.socialmeet.backend.dto.PaymentApiResponse;
import com.socialmeet.backend.enums.PaymentErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 支付相关异常处理器
 */
@RestControllerAdvice
@Slf4j
public class PaymentExceptionHandler {

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<PaymentApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("参数验证失败", ex);
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String errorMessage = "参数验证失败: " + errors.toString();
        PaymentApiResponse<Object> response = PaymentApiResponse.error(
                PaymentErrorCode.INVALID_PARAMETER, errorMessage, null);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<PaymentApiResponse<Object>> handleBindException(BindException ex) {
        log.warn("参数绑定失败", ex);
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String errorMessage = "参数绑定失败: " + errors.toString();
        PaymentApiResponse<Object> response = PaymentApiResponse.error(
                PaymentErrorCode.INVALID_PARAMETER, errorMessage, null);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<PaymentApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("约束违反异常", ex);
        
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        Map<String, String> errors = new HashMap<>();
        
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        
        String errorMessage = "约束违反: " + errors.toString();
        PaymentApiResponse<Object> response = PaymentApiResponse.error(
                PaymentErrorCode.INVALID_PARAMETER, errorMessage, null);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理支付相关业务异常
     */
    @ExceptionHandler(PaymentBusinessException.class)
    public ResponseEntity<PaymentApiResponse<Object>> handlePaymentBusinessException(PaymentBusinessException ex) {
        log.warn("支付业务异常 - code: {}, message: {}", ex.getErrorCode().getCode(), ex.getMessage());
        
        PaymentApiResponse<Object> response = PaymentApiResponse.error(ex.getErrorCode(), ex.getMessage(), null);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<PaymentApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("非法参数异常", ex);
        
        PaymentApiResponse<Object> response = PaymentApiResponse.error(
                PaymentErrorCode.INVALID_PARAMETER, ex.getMessage(), null);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<PaymentApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        log.error("运行时异常", ex);
        
        PaymentApiResponse<Object> response = PaymentApiResponse.error(
                PaymentErrorCode.SYSTEM_ERROR, "系统内部错误", null);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<PaymentApiResponse<Object>> handleException(Exception ex) {
        log.error("未知异常", ex);
        
        PaymentApiResponse<Object> response = PaymentApiResponse.error(
                PaymentErrorCode.SYSTEM_ERROR, "系统内部错误", null);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
