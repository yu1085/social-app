package com.socialmeet.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 支付金额验证注解
 */
@Documented
@Constraint(validatedBy = PaymentAmountValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPaymentAmount {
    
    String message() default "支付金额无效";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * 最小金额
     */
    double min() default 0.01;
    
    /**
     * 最大金额
     */
    double max() default 10000.00;
}
