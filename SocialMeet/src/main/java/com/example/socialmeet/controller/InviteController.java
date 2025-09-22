package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.InviteCodeDTO;
import com.example.socialmeet.entity.InviteCode;
import com.example.socialmeet.entity.InviteRecord;
import com.example.socialmeet.repository.InviteCodeRepository;
import com.example.socialmeet.repository.InviteRecordRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/invite")
@CrossOrigin(originPatterns = "*")
public class InviteController {

    @Autowired
    private InviteCodeRepository inviteCodeRepository;

    @Autowired
    private InviteRecordRepository inviteRecordRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // 获取我的邀请码
    @GetMapping("/code")
    public ResponseEntity<ApiResponse<InviteCodeDTO>> getMyInviteCode(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);

            Optional<InviteCode> inviteCodeOpt = inviteCodeRepository.findByUserId(userId);
            InviteCode inviteCode;
            
            if (inviteCodeOpt.isPresent()) {
                inviteCode = inviteCodeOpt.get();
            } else {
                // 生成新的邀请码
                String code = generateInviteCode();
                inviteCode = new InviteCode(userId, code);
                inviteCode = inviteCodeRepository.save(inviteCode);
            }

            InviteCodeDTO dto = convertToDTO(inviteCode);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取邀请码失败: " + e.getMessage()));
        }
    }

    // 使用邀请码
    @PostMapping("/use")
    public ResponseEntity<ApiResponse<String>> useInviteCode(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String inviteCode) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long inviteeId = jwtUtil.getUserIdFromToken(jwt);

            // 检查是否已经使用过邀请码
            if (inviteRecordRepository.existsByInviteeId(inviteeId)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("您已经使用过邀请码了"));
            }

            // 查找邀请码
            Optional<InviteCode> inviteCodeOpt = inviteCodeRepository.findByInviteCode(inviteCode);
            if (!inviteCodeOpt.isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("邀请码不存在"));
            }

            InviteCode code = inviteCodeOpt.get();
            if (!code.getIsActive()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("邀请码已失效"));
            }

            if (code.getUsageCount() >= code.getMaxUsage()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("邀请码使用次数已达上限"));
            }

            if (code.getExpiresAt().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("邀请码已过期"));
            }

            // 创建邀请记录
            InviteRecord record = new InviteRecord(code.getUserId(), inviteeId, inviteCode);
            record.setRewardAmount(code.getRewardAmount());
            record.setStatus("REWARDED");
            record.setRewardedAt(LocalDateTime.now());
            inviteRecordRepository.save(record);

            // 更新邀请码使用次数
            code.setUsageCount(code.getUsageCount() + 1);
            inviteCodeRepository.save(code);

            return ResponseEntity.ok(ApiResponse.success("邀请码使用成功，获得奖励: " + code.getRewardAmount() + " 金币"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("使用邀请码失败: " + e.getMessage()));
        }
    }

    // 获取我的邀请记录
    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<InviteRecord>>> getMyInviteRecords(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);

            List<InviteRecord> records = inviteRecordRepository.findByInviterId(userId);
            return ResponseEntity.ok(ApiResponse.success(records));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取邀请记录失败: " + e.getMessage()));
        }
    }

    // 获取邀请统计
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getInviteStats(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);

            List<InviteRecord> records = inviteRecordRepository.findByInviterId(userId);
            long totalInvites = records.size();
            long rewardedInvites = records.stream()
                    .filter(r -> r.getStatus().equals("REWARDED"))
                    .count();
            double totalReward = records.stream()
                    .filter(r -> r.getStatus().equals("REWARDED"))
                    .mapToDouble(InviteRecord::getRewardAmount)
                    .sum();

            return ResponseEntity.ok(ApiResponse.success(new InviteStats(totalInvites, rewardedInvites, totalReward)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取邀请统计失败: " + e.getMessage()));
        }
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private InviteCodeDTO convertToDTO(InviteCode inviteCode) {
        InviteCodeDTO dto = new InviteCodeDTO();
        dto.setId(inviteCode.getId());
        dto.setUserId(inviteCode.getUserId());
        dto.setInviteCode(inviteCode.getInviteCode());
        dto.setIsActive(inviteCode.getIsActive());
        dto.setUsageCount(inviteCode.getUsageCount());
        dto.setMaxUsage(inviteCode.getMaxUsage());
        dto.setRewardAmount(inviteCode.getRewardAmount());
        dto.setCreatedAt(inviteCode.getCreatedAt());
        dto.setExpiresAt(inviteCode.getExpiresAt());
        return dto;
    }

    // 内部类用于统计信息
    public static class InviteStats {
        public final long totalInvites;
        public final long rewardedInvites;
        public final double totalReward;

        public InviteStats(long totalInvites, long rewardedInvites, double totalReward) {
            this.totalInvites = totalInvites;
            this.rewardedInvites = rewardedInvites;
            this.totalReward = totalReward;
        }
    }
}