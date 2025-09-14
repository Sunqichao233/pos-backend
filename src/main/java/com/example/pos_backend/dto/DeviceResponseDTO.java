package com.example.pos_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 设备响应DTO
 * 用于返回设备信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponseDTO {

    /**
     * 设备ID
     */
    private Long id;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * MAC地址
     */
    private String macAddress;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 最后在线时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant lastOnline;

    /**
     * 设备状态
     */
    private String status;

    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant registeredAt;

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
