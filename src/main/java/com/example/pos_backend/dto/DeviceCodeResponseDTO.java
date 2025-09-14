package com.example.pos_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 设备码响应DTO
 * 用于返回设备码信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCodeResponseDTO {

    /**
     * 设备码ID
     */
    private Long id;

    /**
     * 设备码（一次性码）
     */
    private String deviceCode;

    /**
     * 关联的设备ID
     */
    private Long deviceId;

    /**
     * 设备指纹信息
     */
    private String deviceFingerprint;

    /**
     * 激活尝试次数
     */
    private Integer activationAttempts;

    /**
     * 最大激活尝试次数
     */
    private Integer maxAttempts;

    /**
     * 设备码状态
     */
    private String status;

    /**
     * 发行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant issuedAt;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant expiredAt;

    /**
     * 绑定时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant boundAt;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updatedAt;

    /**
     * 创建者ID
     */
    private Long createdBy;

    /**
     * 更新者ID
     */
    private Long updatedBy;

    /**
     * 是否已删除
     */
    private Boolean isDeleted;
}
