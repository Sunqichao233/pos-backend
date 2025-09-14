package com.example.pos_backend.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 设备码更新DTO
 * 用于接收更新设备码的请求数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCodeUpdateDTO {

    /**
     * 关联的设备ID
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
     * 绑定时间
     */
    private Instant boundAt;

    /**
     * 更新者ID
     */
    private Long updatedBy;
}
