package com.example.socialmeet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ThirdPartyAuthRequest {
    
    @NotBlank(message = "认证类型不能为空")
    private String authType; // ALIPAY, WECHAT
    
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    @NotBlank(message = "身份证号不能为空")
    private String idCardNumber;
    
    private String phoneNumber;
    
    private String redirectUrl; // 认证完成后的回调URL
    
    private String extraData; // 额外数据，JSON格式
    
    // Constructors
    public ThirdPartyAuthRequest() {}
    
    public ThirdPartyAuthRequest(String authType, String realName, String idCardNumber) {
        this.authType = authType;
        this.realName = realName;
        this.idCardNumber = idCardNumber;
    }
    
    // Getters and Setters
    public String getAuthType() {
        return authType;
    }
    
    public void setAuthType(String authType) {
        this.authType = authType;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getIdCardNumber() {
        return idCardNumber;
    }
    
    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
    
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
    
    public String getExtraData() {
        return extraData;
    }
    
    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}
