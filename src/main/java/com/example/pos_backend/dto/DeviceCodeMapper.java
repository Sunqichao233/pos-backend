package com.example.pos_backend.dto;

import com.example.pos_backend.entity.DeviceCode;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备码映射器
 * 用于DeviceCode实体与DTO之间的转换
 */
@Component
public class DeviceCodeMapper {

    /**
     * 将DeviceCodeRequestDTO转换为DeviceCode实体
     *
     * @param requestDTO 请求DTO
     * @return DeviceCode实体
     */
    public DeviceCode toEntity(DeviceCodeRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        return DeviceCode.builder()
                .deviceId(requestDTO.getDeviceId())
                .deviceFingerprint(requestDTO.getDeviceFingerprint())
                .status(requestDTO.getStatus() != null ? requestDTO.getStatus() : "UNUSED")
                .expiredAt(requestDTO.getExpiredAt())
                .createdBy(requestDTO.getCreatedBy())
                .activationAttempts(0)
                .maxAttempts(3)
                .issuedAt(Instant.now())
                .build();
    }

    /**
     * 将DeviceCodeUpdateDTO转换为DeviceCode实体
     *
     * @param updateDTO 更新DTO
     * @return DeviceCode实体
     */
    public DeviceCode toEntityFromUpdateDTO(DeviceCodeUpdateDTO updateDTO) {
        if (updateDTO == null) {
            return null;
        }

        return DeviceCode.builder()
                .deviceId(updateDTO.getDeviceId())
                .status(updateDTO.getStatus())
                .expiredAt(updateDTO.getExpiredAt())
                .boundAt(updateDTO.getBoundAt())
                .updatedBy(updateDTO.getUpdatedBy())
                .build();
    }

    /**
     * 将DeviceCode实体转换为DeviceCodeResponseDTO
     *
     * @param deviceCode DeviceCode实体
     * @return 响应DTO
     */
    public DeviceCodeResponseDTO toResponseDTO(DeviceCode deviceCode) {
        if (deviceCode == null) {
            return null;
        }

        return DeviceCodeResponseDTO.builder()
                .id(deviceCode.getId())
                .deviceCode(deviceCode.getDeviceCode())
                .deviceId(deviceCode.getDeviceId())
                .deviceFingerprint(deviceCode.getDeviceFingerprint())
                .activationAttempts(deviceCode.getActivationAttempts())
                .maxAttempts(deviceCode.getMaxAttempts())
                .status(deviceCode.getStatus())
                .issuedAt(deviceCode.getIssuedAt())
                .expiredAt(deviceCode.getExpiredAt())
                .boundAt(deviceCode.getBoundAt())
                .createdAt(deviceCode.getCreatedAt())
                .updatedAt(deviceCode.getUpdatedAt())
                .createdBy(deviceCode.getCreatedBy())
                .updatedBy(deviceCode.getUpdatedBy())
                .isDeleted(deviceCode.getIsDeleted())
                .build();
    }

    /**
     * 将DeviceCode实体列表转换为DeviceCodeResponseDTO列表
     *
     * @param deviceCodes DeviceCode实体列表
     * @return 响应DTO列表
     */
    public List<DeviceCodeResponseDTO> toResponseDTOList(List<DeviceCode> deviceCodes) {
        if (deviceCodes == null) {
            return null;
        }

        return deviceCodes.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
