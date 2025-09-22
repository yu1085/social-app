package com.example.socialmeet.controller;

import com.example.socialmeet.service.AlipayApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test/alipay")
@CrossOrigin(originPatterns = "*")
public class AlipayTestController {
    
    @Autowired
    private AlipayApiService alipayApiService;
    
    /**
     * 测试支付宝API连接
     */
    @GetMapping("/connection")
    public Map<String, Object> testConnection() {
        return alipayApiService.testConnection();
    }
    
    /**
     * 测试身份证二要素核验
     */
    @PostMapping("/verify")
    public Map<String, Object> testVerifyIdCard(
            @RequestParam String certName,
            @RequestParam String certNo) {
        return alipayApiService.verifyIdCard(certName, certNo);
    }
}
