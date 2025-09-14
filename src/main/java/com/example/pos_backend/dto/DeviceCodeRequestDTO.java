package com.example.pos_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 设备码请求DTO
 * 用于接收创建设备码的请求数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCodeRequestDTO {

    /**
     * 设备码（一次性码）
     */
    @NotBlank(message = "设备码不能为空")
    @Size(max = 64, message = "设备码长度不能超过64个字符")
    private String deviceCode;

    /**
     * 关联的设备ID（可选，支持预发行）
     */
    private Long deviceId;

    /**
     * 设备码状态
     */
    @Size(max = 20, message = "状态长度不能超过20个字符")
    private String status;

    /**
     * 过期时间
     */
    private Instant expiredAt;

    /**
     * 创建者ID
     */
    private Long createdBy;
}
