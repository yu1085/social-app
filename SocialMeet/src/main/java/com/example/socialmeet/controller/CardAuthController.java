package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.service.CmccCardAuthService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/card")
@CrossOrigin(originPatterns = "*")
public class CardAuthController {

    @Autowired
    private CmccCardAuthService cardAuthService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 号卡认证验证
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyCardAuth(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            String phoneNumber = request.get("phoneNumber");
            String scenario = request.getOrDefault("scenario", "MOBILE");
            String networkType = request.getOrDefault("networkType", "MOBILE");
            
            if (phoneNumber == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("手机号不能为空"));
            }
            
            // 执行号卡认证
            CmccCardAuthService.CardAuthResult result = 
                cardAuthService.performCardAuth(phoneNumber, scenario, networkType);
            
            if (result.isSuccess()) {
                Map<String, Object> responseData = Map.of(
                    "success", true,
                    "message", "号卡认证成功",
                    "phoneNumber", result.getPhoneNumber(),
                    "authToken", result.getAuthToken(),
                    "expireTime", result.getExpireTime(),
                    "authMethod", result.getAuthMethod(),
                    "scenario", result.getScenario(),
                    "networkType", result.getNetworkType()
                );
                return ResponseEntity.ok(ApiResponse.success(responseData));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("认证失败: " + result.getMessage()));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("认证失败: " + e.getMessage()));
        }
    }

    /**
     * 获取认证能力信息
     */
    @PostMapping("/capability")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAuthCapability(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            String phoneNumber = request.get("phoneNumber");
            
            if (phoneNumber == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("手机号不能为空"));
            }
            
            // 获取认证能力信息
            CmccCardAuthService.AuthCapability capability = 
                cardAuthService.getAuthCapability(phoneNumber);
            
            if (capability != null) {
                Map<String, Object> responseData = Map.of(
                    "phoneNumber", capability.getPhoneNumber(),
                    "mobileAuthSupported", capability.isMobileAuthSupported(),
                    "simAuthSupported", capability.isSimAuthSupported(),
                    "carrier", capability.getCarrier(),
                    "region", capability.getRegion(),
                    "supportedScenarios", capability.getSupportedScenarios()
                );
                return ResponseEntity.ok(ApiResponse.success(responseData));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取认证能力信息失败"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取认证能力信息失败: " + e.getMessage()));
        }
    }

    /**
     * 检查网络环境支持
     */
    @GetMapping("/network-support")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkNetworkSupport(
            @RequestParam String networkType) {
        try {
            boolean supported = cardAuthService.isNetworkSupported(networkType);
            Map<String, Object> responseData = Map.of(
                "networkType", networkType,
                "supported", supported,
                "message", supported ? "网络类型支持" : "网络类型不支持"
            );
            return ResponseEntity.ok(ApiResponse.success(responseData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查网络支持失败: " + e.getMessage()));
        }
    }

    /**
     * 检查场景支持
     */
    @GetMapping("/scenario-support")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkScenarioSupport(
            @RequestParam String scenario) {
        try {
            boolean supported = cardAuthService.isScenarioSupported(scenario);
            Map<String, Object> responseData = Map.of(
                "scenario", scenario,
                "supported", supported,
                "message", supported ? "场景支持" : "场景不支持"
            );
            return ResponseEntity.ok(ApiResponse.success(responseData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查场景支持失败: " + e.getMessage()));
        }
    }

    /**
     * 检查号卡认证配置状态
     */
    @GetMapping("/config-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConfigStatus() {
        try {
            boolean isConfigured = cardAuthService.isConfigured();
            Map<String, Object> responseData = Map.of(
                "configured", isConfigured,
                "message", isConfigured ? "配置正常" : "请配置号卡认证参数",
                "serviceType", "CARD_AUTH",
                "supportedScenarios", new String[]{"PC", "MOBILE", "H5", "SHORT_LINK"}
            );
            return ResponseEntity.ok(ApiResponse.success(responseData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查配置失败: " + e.getMessage()));
        }
    }
}
