package com.socialmeet.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * 支付金额验证器
 */
public class PaymentAmountValidator implements ConstraintValidator<ValidPaymentAmount, BigDecimal> {
    
    private double min;
    private double max;
    
    @Override
    public void initialize(ValidPaymentAmount constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }
    
    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        
        // 检查金额范围
        if (value.compareTo(BigDecimal.valueOf(min)) < 0 || 
            value.compareTo(BigDecimal.valueOf(max)) > 0) {
            return false;
        }
        
        // 检查小数位数（最多2位小数）
        if (value.scale() > 2) {
            return false;
        }
        
        return true;
    }
}
