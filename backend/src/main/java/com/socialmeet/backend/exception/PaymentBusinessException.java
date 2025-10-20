package com.socialmeet.backend.exception;

import com.socialmeet.backend.enums.PaymentErrorCode;
import lombok.Getter;

/**
 * 支付业务异常
 */
@Getter
public class PaymentBusinessException extends RuntimeException {
    
    private final PaymentErrorCode errorCode;
    
    public PaymentBusinessException(PaymentErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public PaymentBusinessException(PaymentErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public PaymentBusinessException(PaymentErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
