package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.WealthLevelDTO;
import com.example.socialmeet.service.WealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wealth")
@CrossOrigin(origins = "*")
public class WealthController {
    
    @Autowired
    private WealthService wealthService;
    
    @GetMapping("/levels")
    public ResponseEntity<ApiResponse<List<WealthLevelDTO>>> getAllWealthLevels() {
        try {
            List<WealthLevelDTO> levels = wealthService.getAllWealthLevels();
            return ResponseEntity.ok(ApiResponse.success(levels));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取财富等级失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/levels/{id}")
    public ResponseEntity<ApiResponse<WealthLevelDTO>> getWealthLevelById(@PathVariable Long id) {
        try {
            WealthLevelDTO level = wealthService.getWealthLevelById(id);
            if (level == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("财富等级不存在"));
            }
            return ResponseEntity.ok(ApiResponse.success(level));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取财富等级失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/level-by-contribution")
    public ResponseEntity<ApiResponse<WealthLevelDTO>> getWealthLevelByContribution(
            @RequestParam BigDecimal contribution) {
        try {
            WealthLevelDTO level = wealthService.getWealthLevelByContribution(contribution);
            return ResponseEntity.ok(ApiResponse.success(level));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取财富等级失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/higher-levels/{currentLevel}")
    public ResponseEntity<ApiResponse<List<WealthLevelDTO>>> getHigherWealthLevels(
            @PathVariable Integer currentLevel) {
        try {
            List<WealthLevelDTO> levels = wealthService.getHigherWealthLevels(currentLevel);
            return ResponseEntity.ok(ApiResponse.success(levels));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取更高财富等级失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user-level")
    public ResponseEntity<ApiResponse<Integer>> getUserWealthLevel(
            @RequestParam BigDecimal totalContribution) {
        try {
            Integer level = wealthService.getUserWealthLevel(totalContribution);
            return ResponseEntity.ok(ApiResponse.success(level));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户财富等级失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user-level-name")
    public ResponseEntity<ApiResponse<String>> getUserWealthLevelName(
            @RequestParam BigDecimal totalContribution) {
        try {
            String levelName = wealthService.getUserWealthLevelName(totalContribution);
            return ResponseEntity.ok(ApiResponse.success(levelName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户财富等级名称失败: " + e.getMessage()));
        }
    }
}
