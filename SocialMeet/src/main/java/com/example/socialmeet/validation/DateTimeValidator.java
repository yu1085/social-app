package com.example.socialmeet.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

/**
 * 日期时间验证器
 * 验证LocalDateTime字段是否符合要求
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
public class DateTimeValidator implements ConstraintValidator<ValidDateTime, LocalDateTime> {
    
    private boolean allowNull;
    private boolean allowFuture;
    private boolean allowPast;
    
    @Override
    public void initialize(ValidDateTime constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
        this.allowFuture = constraintAnnotation.allowFuture();
        this.allowPast = constraintAnnotation.allowPast();
    }
    
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        // 如果允许null且值为null，则有效
        if (allowNull && value == null) {
            return true;
        }
        
        // 如果不允许null且值为null，则无效
        if (!allowNull && value == null) {
            return false;
        }
        
        // 检查是否为未来时间
        if (!allowFuture && value.isAfter(LocalDateTime.now())) {
            return false;
        }
        
        // 检查是否为过去时间
        if (!allowPast && value.isBefore(LocalDateTime.now())) {
            return false;
        }
        
        return true;
    }
}
