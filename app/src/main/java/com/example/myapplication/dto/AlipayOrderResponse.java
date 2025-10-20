package com.example.myapplication.dto;

/**
 * 支付宝订单响应DTO
 */
public class AlipayOrderResponse {
    private String alipayOrderInfo;
    
    public AlipayOrderResponse() {}
    
    public AlipayOrderResponse(String alipayOrderInfo) {
        this.alipayOrderInfo = alipayOrderInfo;
    }
    
    public String getAlipayOrderInfo() {
        return alipayOrderInfo;
    }
    
    public void setAlipayOrderInfo(String alipayOrderInfo) {
        this.alipayOrderInfo = alipayOrderInfo;
    }
}
