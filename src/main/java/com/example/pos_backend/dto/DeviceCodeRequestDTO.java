package com.example.pos_backend.dto;

import jakarta.validation.constraints.NotNull;
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
     * 关联的设备ID（必填，Square风格按需生成）
     */
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    /**
     * 设备指纹信息
     */
    @Size(max = 255, message = "设备指纹长度不能超过255个字符")
    private String deviceFingerprint;

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
