package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.GuardRelationshipDTO;
import com.example.socialmeet.service.GuardService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/guard")
@CrossOrigin(origins = "*")
public class GuardController {
    
    @Autowired
    private GuardService guardService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/become")
    public ResponseEntity<ApiResponse<GuardRelationshipDTO>> becomeGuardian(
            @RequestHeader("Authorization") String token,
            @RequestParam Long protectedId) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long guardianId = jwtUtil.getUserIdFromToken(jwt);
            
            GuardRelationshipDTO relationship = guardService.becomeGuardian(guardianId, protectedId);
            return ResponseEntity.ok(ApiResponse.success(relationship));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("成为守护者失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/stop")
    public ResponseEntity<ApiResponse<String>> stopGuarding(
            @RequestHeader("Authorization") String token,
            @RequestParam Long protectedId) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long guardianId = jwtUtil.getUserIdFromToken(jwt);
            
            boolean success = guardService.stopGuarding(guardianId, protectedId);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("停止守护成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("停止守护失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("停止守护失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/guardians")
    public ResponseEntity<ApiResponse<List<GuardRelationshipDTO>>> getGuardians(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            List<GuardRelationshipDTO> guardians = guardService.getGuardiansByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(guardians));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取守护者列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<List<GuardRelationshipDTO>>> getProtectedUsers(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long guardianId = jwtUtil.getUserIdFromToken(jwt);
            
            List<GuardRelationshipDTO> protectedUsers = guardService.getProtectedUsersByGuardianId(guardianId);
            return ResponseEntity.ok(ApiResponse.success(protectedUsers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取被守护用户列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<List<GuardRelationshipDTO>>> getGuardIncomeRanking() {
        try {
            List<GuardRelationshipDTO> ranking = guardService.getGuardIncomeRanking();
            return ResponseEntity.ok(ApiResponse.success(ranking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取守护收入榜失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/income")
    public ResponseEntity<ApiResponse<BigDecimal>> getGuardianIncome(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long guardianId = jwtUtil.getUserIdFromToken(jwt);
            
            BigDecimal income = guardService.getGuardianIncome(guardianId);
            return ResponseEntity.ok(ApiResponse.success(income));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取守护收入失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/contribute")
    public ResponseEntity<ApiResponse<String>> addContribution(
            @RequestHeader("Authorization") String token,
            @RequestParam Long protectedId,
            @RequestParam BigDecimal amount) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long guardianId = jwtUtil.getUserIdFromToken(jwt);
            
            boolean success = guardService.addContribution(guardianId, protectedId, amount);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("贡献成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("贡献失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("贡献失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkGuardianStatus(
            @RequestHeader("Authorization") String token,
            @RequestParam Long protectedId) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long guardianId = jwtUtil.getUserIdFromToken(jwt);
            
            boolean isGuardian = guardService.isGuardian(guardianId, protectedId);
            return ResponseEntity.ok(ApiResponse.success(isGuardian));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("检查守护状态失败: " + e.getMessage()));
        }
    }
}
