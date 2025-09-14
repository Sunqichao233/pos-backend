package com.example.pos_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备激活请求DTO
 * 用于用户自助激活设备
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceActivationRequestDTO {

    /**
     * 激活码（12位数字字母混合）
     */
    @NotBlank(message = "激活码不能为空")
    @Size(min = 12, max = 12, message = "激活码必须为12位")
    private String activationCode;

    /**
     * 设备指纹信息（必填）
     */
    @NotBlank(message = "设备指纹不能为空")
    @Size(max = 255, message = "设备指纹长度不能超过255个字符")
    private String deviceFingerprint;
}
