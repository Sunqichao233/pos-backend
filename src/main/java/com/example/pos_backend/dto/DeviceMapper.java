package com.example.pos_backend.dto;

import com.example.pos_backend.entity.Device;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备映射器
 * 用于Device实体与DTO之间的转换
 */
@Component
public class DeviceMapper {

    /**
     * 将DeviceRequestDTO转换为Device实体
     *
     * @param requestDTO 请求DTO
     * @return Device实体
     */
    public Device toEntity(DeviceRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        return Device.builder()
                .deviceName(requestDTO.getDeviceName())
                .deviceType(requestDTO.getDeviceType())
                .macAddress(requestDTO.getMacAddress())
                .ipAddress(requestDTO.getIpAddress())
                .status(requestDTO.getStatus() != null ? requestDTO.getStatus() : "OFFLINE")
                .registeredAt(requestDTO.getRegisteredAt())
                .createdBy(requestDTO.getCreatedBy())
                .build();
    }

    /**
     * 将DeviceUpdateDTO转换为Device实体
     *
     * @param updateDTO 更新DTO
     * @return Device实体
     */
    public Device toEntityFromUpdateDTO(DeviceUpdateDTO updateDTO) {
        if (updateDTO == null) {
            return null;
        }

        return Device.builder()
                .deviceName(updateDTO.getDeviceName())
                .deviceType(updateDTO.getDeviceType())
                .macAddress(updateDTO.getMacAddress())
                .ipAddress(updateDTO.getIpAddress())
                .status(updateDTO.getStatus())
                .lastOnline(updateDTO.getLastOnline())
                .updatedBy(updateDTO.getUpdatedBy())
                .build();
    }

    /**
     * 将Device实体转换为DeviceResponseDTO
     *
     * @param device Device实体
     * @return 响应DTO
     */
    public DeviceResponseDTO toResponseDTO(Device device) {
        if (device == null) {
            return null;
        }

        return DeviceResponseDTO.builder()
                .id(device.getId())
                .deviceName(device.getDeviceName())
                .deviceType(device.getDeviceType())
                .macAddress(device.getMacAddress())
                .ipAddress(device.getIpAddress())
                .lastOnline(device.getLastOnline())
                .status(device.getStatus())
                .registeredAt(device.getRegisteredAt())
                .createdAt(device.getCreatedAt())
                .updatedAt(device.getUpdatedAt())
                .createdBy(device.getCreatedBy())
                .updatedBy(device.getUpdatedBy())
                .isDeleted(device.getIsDeleted())
                .build();
    }

    /**
     * 将Device实体列表转换为DeviceResponseDTO列表
     *
     * @param devices Device实体列表
     * @return 响应DTO列表
     */
    public List<DeviceResponseDTO> toResponseDTOList(List<Device> devices) {
        if (devices == null) {
            return null;
        }

        return devices.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
