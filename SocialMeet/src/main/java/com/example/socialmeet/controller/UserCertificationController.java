package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.UserCertificationDTO;
import com.example.socialmeet.entity.UserCertification;
import com.example.socialmeet.repository.UserCertificationRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users/certification")
@CrossOrigin(originPatterns = "*")
public class UserCertificationController {

    @Autowired
    private UserCertificationRepository certificationRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // 获取我的认证状态
    @GetMapping
    public ResponseEntity<ApiResponse<UserCertificationDTO>> getMyCertification(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);

            Optional<UserCertification> certificationOpt = certificationRepository.findByUserId(userId);
            if (certificationOpt.isPresent()) {
                UserCertification certification = certificationOpt.get();
                UserCertificationDTO dto = convertToDTO(certification);
                return ResponseEntity.ok(ApiResponse.success(dto));
            } else {
                return ResponseEntity.ok(ApiResponse.success(null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取认证状态失败: " + e.getMessage()));
        }
    }

    // 提交认证申请
    @PostMapping
    public ResponseEntity<ApiResponse<UserCertificationDTO>> submitCertification(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserCertificationDTO certificationDTO) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);

            // 检查是否已有认证申请
            Optional<UserCertification> existingOpt = certificationRepository.findByUserId(userId);
            if (existingOpt.isPresent()) {
                UserCertification existing = existingOpt.get();
                if (existing.getStatus().equals("PENDING")) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("您已有待审核的认证申请"));
                }
            }

            UserCertification certification = new UserCertification();
            certification.setUserId(userId);
            certification.setCertificationType(certificationDTO.getCertificationType());
            certification.setRealName(certificationDTO.getRealName());
            certification.setIdCardNumber(certificationDTO.getIdCardNumber());
            certification.setPhoneNumber(certificationDTO.getPhoneNumber());
            certification.setIdCardFrontImage(certificationDTO.getIdCardFrontImage());
            certification.setIdCardBackImage(certificationDTO.getIdCardBackImage());
            certification.setSelfieImage(certificationDTO.getSelfieImage());
            certification.setStatus("PENDING");
            certification.setSubmittedAt(LocalDateTime.now());

            UserCertification savedCertification = certificationRepository.save(certification);
            UserCertificationDTO dto = convertToDTO(savedCertification);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("提交认证申请失败: " + e.getMessage()));
        }
    }

    // 更新认证申请
    @PutMapping
    public ResponseEntity<ApiResponse<UserCertificationDTO>> updateCertification(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserCertificationDTO certificationDTO) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);

            Optional<UserCertification> certificationOpt = certificationRepository.findByUserId(userId);
            if (!certificationOpt.isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("未找到认证申请"));
            }

            UserCertification certification = certificationOpt.get();
            if (!certification.getStatus().equals("PENDING")) {
                return ResponseEntity.badRequest().body(ApiResponse.error("只能修改待审核的认证申请"));
            }

            // 更新认证信息
            if (certificationDTO.getRealName() != null) {
                certification.setRealName(certificationDTO.getRealName());
            }
            if (certificationDTO.getIdCardNumber() != null) {
                certification.setIdCardNumber(certificationDTO.getIdCardNumber());
            }
            if (certificationDTO.getPhoneNumber() != null) {
                certification.setPhoneNumber(certificationDTO.getPhoneNumber());
            }
            if (certificationDTO.getIdCardFrontImage() != null) {
                certification.setIdCardFrontImage(certificationDTO.getIdCardFrontImage());
            }
            if (certificationDTO.getIdCardBackImage() != null) {
                certification.setIdCardBackImage(certificationDTO.getIdCardBackImage());
            }
            if (certificationDTO.getSelfieImage() != null) {
                certification.setSelfieImage(certificationDTO.getSelfieImage());
            }

            UserCertification savedCertification = certificationRepository.save(certification);
            UserCertificationDTO dto = convertToDTO(savedCertification);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新认证申请失败: " + e.getMessage()));
        }
    }

    // 获取认证历史
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<UserCertificationDTO>>> getCertificationHistory(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);

            List<UserCertification> certifications = certificationRepository.findByUserIdAndCertificationType(userId, null);
            List<UserCertificationDTO> dtos = certifications.stream()
                    .map(this::convertToDTO)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success(dtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取认证历史失败: " + e.getMessage()));
        }
    }

    private UserCertificationDTO convertToDTO(UserCertification certification) {
        UserCertificationDTO dto = new UserCertificationDTO();
        dto.setId(certification.getId());
        dto.setUserId(certification.getUserId());
        dto.setCertificationType(certification.getCertificationType());
        dto.setRealName(certification.getRealName());
        dto.setIdCardNumber(certification.getIdCardNumber());
        dto.setPhoneNumber(certification.getPhoneNumber());
        dto.setIdCardFrontImage(certification.getIdCardFrontImage());
        dto.setIdCardBackImage(certification.getIdCardBackImage());
        dto.setSelfieImage(certification.getSelfieImage());
        dto.setStatus(certification.getStatus());
        dto.setRejectReason(certification.getRejectReason());
        dto.setSubmittedAt(certification.getSubmittedAt());
        dto.setReviewedAt(certification.getReviewedAt());
        dto.setCreatedAt(certification.getCreatedAt());
        return dto;
    }
}
