package com.example.pos_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 设备请求DTO
 * 用于接收创建设备的请求数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRequestDTO {

    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空")
    @Size(max = 100, message = "设备名称长度不能超过100个字符")
    private String deviceName;

    /**
     * 设备类型
     */
    @NotBlank(message = "设备类型不能为空")
    @Size(max = 50, message = "设备类型长度不能超过50个字符")
    private String deviceType;

    /**
     * MAC地址
     */
    @Size(max = 17, message = "MAC地址长度不能超过17个字符")
    @Pattern(regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$", message = "MAC地址格式不正确")
    private String macAddress;

    /**
     * IP地址
     */
    @Size(max = 15, message = "IP地址长度不能超过15个字符")
    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$", 
             message = "IP地址格式不正确")
    private String ipAddress;

    /**
     * 设备状态
     */
    @Size(max = 20, message = "状态长度不能超过20个字符")
    private String status;

    /**
     * 注册时间
     */
    private Instant registeredAt;

    /**
     * 创建者ID
     */
    private Long createdBy;
}
