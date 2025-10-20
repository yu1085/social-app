package com.socialmeet.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户设置DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsDTO {

    private Boolean voiceCallEnabled;
    private Boolean videoCallEnabled;
    private Boolean messageChargeEnabled;
    private Double voiceCallPrice;
    private Double videoCallPrice;
    private Double messagePrice;
}
