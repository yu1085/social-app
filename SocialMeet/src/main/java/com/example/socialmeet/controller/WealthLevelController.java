package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.WealthLevelDTO;
import com.example.socialmeet.entity.WealthLevel;
import com.example.socialmeet.service.WealthLevelService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wealth-level")
@CrossOrigin(originPatterns = "*")
public class WealthLevelController {
    
    @Autowired
    private WealthLevelService wealthLevelService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取用户财富等级信息
     */
    @GetMapping("/my-level")
    public ResponseEntity<ApiResponse<WealthLevelDTO>> getMyWealthLevel(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            WealthLevel wealthLevel = wealthLevelService.getUserWealthLevel(userId);
            WealthLevelDTO dto = new WealthLevelDTO(wealthLevel);
            
            // 获取用户特权
            List<WealthLevel.PrivilegeType> privileges = wealthLevelService.getUserPrivileges(userId);
            dto.setPrivileges(privileges);
            
            // 获取用户排名
            Long userRank = wealthLevelService.getUserRank(userId);
            dto.setUserRank(userRank);
            
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取财富等级失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户等级进度信息
     */
    @GetMapping("/progress")
    public ResponseEntity<ApiResponse<WealthLevelService.LevelProgressInfo>> getLevelProgress(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            WealthLevelService.LevelProgressInfo progressInfo = wealthLevelService.getLevelProgress(userId);
            return ResponseEntity.ok(ApiResponse.success(progressInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取等级进度失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户特权列表
     */
    @GetMapping("/privileges")
    public ResponseEntity<ApiResponse<List<String>>> getUserPrivileges(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            List<String> privileges = wealthLevelService.getUserPrivilegeNames(userId);
            return ResponseEntity.ok(ApiResponse.success(privileges));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户特权失败: " + e.getMessage()));
        }
    }
    
    /**
     * 检查用户是否有特定特权
     */
    @GetMapping("/has-privilege/{privilege}")
    public ResponseEntity<ApiResponse<Boolean>> hasPrivilege(
            @RequestHeader("Authorization") String token,
            @PathVariable String privilege) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            WealthLevel.PrivilegeType privilegeType = WealthLevel.PrivilegeType.valueOf(privilege.toUpperCase());
            boolean hasPrivilege = wealthLevelService.hasPrivilege(userId, privilegeType);
            
            return ResponseEntity.ok(ApiResponse.success(hasPrivilege));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("检查特权失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取财富排行榜
     */
    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<List<WealthLevelDTO>>> getWealthRanking(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<WealthLevel> wealthLevels = wealthLevelService.getWealthRanking(limit);
            List<WealthLevelDTO> dtos = wealthLevels.stream()
                    .map(WealthLevelDTO::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(dtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取排行榜失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取指定等级的用户列表
     */
    @GetMapping("/level/{levelName}")
    public ResponseEntity<ApiResponse<List<WealthLevelDTO>>> getUsersByLevel(
            @PathVariable String levelName) {
        try {
            List<WealthLevel> wealthLevels = wealthLevelService.getUsersByLevel(levelName);
            List<WealthLevelDTO> dtos = wealthLevels.stream()
                    .map(WealthLevelDTO::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(dtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取等级用户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取特权用户列表
     */
    @GetMapping("/privileged/{privilege}")
    public ResponseEntity<ApiResponse<List<WealthLevelDTO>>> getPrivilegedUsers(
            @PathVariable String privilege) {
        try {
            WealthLevel.PrivilegeType privilegeType = WealthLevel.PrivilegeType.valueOf(privilege.toUpperCase());
            List<WealthLevel> wealthLevels = wealthLevelService.getPrivilegedUsers(privilegeType);
            List<WealthLevelDTO> dtos = wealthLevels.stream()
                    .map(WealthLevelDTO::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(dtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取特权用户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取所有等级规则
     */
    @GetMapping("/rules")
    public ResponseEntity<ApiResponse<List<LevelRuleDTO>>> getLevelRules() {
        try {
            List<LevelRuleDTO> rules = List.of(
                new LevelRuleDTO("黑钻", "💎", "#000000", 1000000, null, "最高等级特权"),
                new LevelRuleDTO("金钻", "💎", "#FFD700", 700000, 999999, "高级特权"),
                new LevelRuleDTO("红钻", "💎", "#FF69B4", 500000, 699999, "免费VIP/SVIP、免费特效"),
                new LevelRuleDTO("橙钻", "💎", "#FF8C00", 300000, 499999, "专属特效、靓号定制、专属礼物"),
                new LevelRuleDTO("紫钻", "💎", "#8A2BE2", 100000, 299999, "高级特权"),
                new LevelRuleDTO("蓝钻", "💎", "#1E90FF", 50000, 99999, "中级特权"),
                new LevelRuleDTO("青钻", "💎", "#00CED1", 30000, 49999, "专属客服服务"),
                new LevelRuleDTO("铂金", "💎", "#C0C0C0", 10000, 29999, "中级特权"),
                new LevelRuleDTO("黄金", "💎", "#FFD700", 5000, 9999, "基础特权"),
                new LevelRuleDTO("白银", "💎", "#C0C0C0", 2000, 4999, "购买折扣、每周促销"),
                new LevelRuleDTO("青铜", "💎", "#CD7F32", 1000, 1999, "靓号折扣、每周促销"),
                new LevelRuleDTO("普通", "⭐", "#808080", 0, 999, "基础用户")
            );
            
            return ResponseEntity.ok(ApiResponse.success(rules));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取等级规则失败: " + e.getMessage()));
        }
    }
    
    /**
     * 等级规则DTO
     */
    public static class LevelRuleDTO {
        private String levelName;
        private String levelIcon;
        private String levelColor;
        private Integer minWealthValue;
        private Integer maxWealthValue;
        private String description;
        
        public LevelRuleDTO(String levelName, String levelIcon, String levelColor, 
                           Integer minWealthValue, Integer maxWealthValue, String description) {
            this.levelName = levelName;
            this.levelIcon = levelIcon;
            this.levelColor = levelColor;
            this.minWealthValue = minWealthValue;
            this.maxWealthValue = maxWealthValue;
            this.description = description;
        }
        
        // Getters and Setters
        public String getLevelName() { return levelName; }
        public void setLevelName(String levelName) { this.levelName = levelName; }
        
        public String getLevelIcon() { return levelIcon; }
        public void setLevelIcon(String levelIcon) { this.levelIcon = levelIcon; }
        
        public String getLevelColor() { return levelColor; }
        public void setLevelColor(String levelColor) { this.levelColor = levelColor; }
        
        public Integer getMinWealthValue() { return minWealthValue; }
        public void setMinWealthValue(Integer minWealthValue) { this.minWealthValue = minWealthValue; }
        
        public Integer getMaxWealthValue() { return maxWealthValue; }
        public void setMaxWealthValue(Integer maxWealthValue) { this.maxWealthValue = maxWealthValue; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
