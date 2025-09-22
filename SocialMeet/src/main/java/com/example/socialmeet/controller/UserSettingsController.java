package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.UserSettingsDTO;
import com.example.socialmeet.entity.UserSettings;
import com.example.socialmeet.repository.UserSettingsRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users/settings")
@CrossOrigin(originPatterns = "*")
public class UserSettingsController {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // 获取用户设置
    @GetMapping
    public ResponseEntity<ApiResponse<UserSettingsDTO>> getUserSettings(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);

            Optional<UserSettings> settingsOpt = userSettingsRepository.findByUserId(userId);
            if (settingsOpt.isPresent()) {
                UserSettings settings = settingsOpt.get();
                UserSettingsDTO dto = convertToDTO(settings);
                return ResponseEntity.ok(ApiResponse.success(dto));
            } else {
                // 创建默认设置
                UserSettings defaultSettings = new UserSettings(userId);
                UserSettings savedSettings = userSettingsRepository.save(defaultSettings);
                UserSettingsDTO dto = convertToDTO(savedSettings);
                return ResponseEntity.ok(ApiResponse.success(dto));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户设置失败: " + e.getMessage()));
        }
    }

    // 更新用户设置
    @PutMapping
    public ResponseEntity<ApiResponse<UserSettingsDTO>> updateUserSettings(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserSettingsDTO settingsDTO) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);

            Optional<UserSettings> settingsOpt = userSettingsRepository.findByUserId(userId);
            UserSettings settings;
            
            if (settingsOpt.isPresent()) {
                settings = settingsOpt.get();
                updateSettingsFromDTO(settings, settingsDTO);
            } else {
                settings = new UserSettings(userId);
                updateSettingsFromDTO(settings, settingsDTO);
            }

            UserSettings savedSettings = userSettingsRepository.save(settings);
            UserSettingsDTO dto = convertToDTO(savedSettings);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新用户设置失败: " + e.getMessage()));
        }
    }


    private UserSettingsDTO convertToDTO(UserSettings settings) {
        UserSettingsDTO dto = new UserSettingsDTO();
        dto.setId(settings.getId());
        dto.setUserId(settings.getUserId());
        dto.setCallEnabled(settings.getCallEnabled());
        dto.setCallPricePerMinute(settings.getCallPricePerMinute());
        dto.setCallMinDuration(settings.getCallMinDuration());
        dto.setVideoCallEnabled(settings.getVideoCallEnabled());
        dto.setVideoCallPricePerMinute(settings.getVideoCallPricePerMinute());
        dto.setVoiceCallEnabled(settings.getVoiceCallEnabled());
        dto.setVoiceCallPricePerMinute(settings.getVoiceCallPricePerMinute());
        dto.setMessagePricePerMessage(settings.getMessagePricePerMessage());
        dto.setAutoAcceptCalls(settings.getAutoAcceptCalls());
        dto.setShowOnlineStatus(settings.getShowOnlineStatus());
        dto.setAllowStrangerCalls(settings.getAllowStrangerCalls());
        return dto;
    }

    private void updateSettingsFromDTO(UserSettings settings, UserSettingsDTO dto) {
        if (dto.getCallEnabled() != null) settings.setCallEnabled(dto.getCallEnabled());
        if (dto.getCallPricePerMinute() != null) settings.setCallPricePerMinute(dto.getCallPricePerMinute());
        if (dto.getCallMinDuration() != null) settings.setCallMinDuration(dto.getCallMinDuration());
        if (dto.getVideoCallEnabled() != null) settings.setVideoCallEnabled(dto.getVideoCallEnabled());
        if (dto.getVideoCallPricePerMinute() != null) settings.setVideoCallPricePerMinute(dto.getVideoCallPricePerMinute());
        if (dto.getVoiceCallEnabled() != null) settings.setVoiceCallEnabled(dto.getVoiceCallEnabled());
        if (dto.getVoiceCallPricePerMinute() != null) settings.setVoiceCallPricePerMinute(dto.getVoiceCallPricePerMinute());
        if (dto.getMessagePricePerMessage() != null) settings.setMessagePricePerMessage(dto.getMessagePricePerMessage());
        if (dto.getAutoAcceptCalls() != null) settings.setAutoAcceptCalls(dto.getAutoAcceptCalls());
        if (dto.getShowOnlineStatus() != null) settings.setShowOnlineStatus(dto.getShowOnlineStatus());
        if (dto.getAllowStrangerCalls() != null) settings.setAllowStrangerCalls(dto.getAllowStrangerCalls());
    }
}
