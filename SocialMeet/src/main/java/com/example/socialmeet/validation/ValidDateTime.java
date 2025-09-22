
package com.example.socialmeet.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 日期时间验证注解
 * 用于验证LocalDateTime字段是否为有效值
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeValidator.class)
@Documented
public @interface ValidDateTime {
    
    String message() default "日期时间值无效";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * 是否允许null值
     */
    boolean allowNull() default false;
    
    /**
     * 是否允许未来时间
     */
    boolean allowFuture() default true;
    
    /**
     * 是否允许过去时间
     */
    boolean allowPast() default true;
}
