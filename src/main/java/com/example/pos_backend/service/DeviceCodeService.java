package com.example.pos_backend.service;

import com.example.pos_backend.constants.GlobalConstants;
import com.example.pos_backend.dto.DeviceCodeMapper;
import com.example.pos_backend.dto.DeviceCodeRequestDTO;
import com.example.pos_backend.dto.DeviceCodeResponseDTO;
import com.example.pos_backend.dto.DeviceCodeUpdateDTO;
import com.example.pos_backend.entity.DeviceCode;
import com.example.pos_backend.exception.DeviceCodeNotFoundException;
import com.example.pos_backend.repository.DeviceCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 设备码服务类
 * 提供设备码相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeviceCodeService {

    private final DeviceCodeRepository deviceCodeRepository;
    private final DeviceCodeMapper deviceCodeMapper;

    /**
     * 创建新设备码
     *
     * @param requestDTO 设备码创建请求
     * @return 创建的设备码响应DTO
     */
    public DeviceCodeResponseDTO createDeviceCode(DeviceCodeRequestDTO requestDTO) {
        log.info("创建新设备码: {}", requestDTO.getDeviceCode());
        
        // 检查设备码是否已存在
        if (deviceCodeRepository.findByDeviceCode(requestDTO.getDeviceCode()).isPresent()) {
            throw new RuntimeException("设备码已存在: " + requestDTO.getDeviceCode());
        }
        
        DeviceCode deviceCode = deviceCodeMapper.toEntity(requestDTO);
        
        // 设置创建时间和更新时间
        Instant now = Instant.now();
        deviceCode.setCreatedAt(now);
        deviceCode.setUpdatedAt(now);
        deviceCode.setIsDeleted(GlobalConstants.Database.NOT_DELETED);
        
        // 设置默认发行时间
        if (deviceCode.getIssuedAt() == null) {
            deviceCode.setIssuedAt(now);
        }
        
        DeviceCode savedDeviceCode = deviceCodeRepository.save(deviceCode);
        log.info("设备码创建成功，ID: {}", savedDeviceCode.getId());
        
        return deviceCodeMapper.toResponseDTO(savedDeviceCode);
    }

    /**
     * 根据ID获取设备码
     *
     * @param id 设备码ID
     * @return 设备码响应DTO
     * @throws DeviceCodeNotFoundException 如果设备码不存在
     */
    @Transactional(readOnly = true)
    public DeviceCodeResponseDTO getDeviceCodeById(Long id) {
        log.info("根据ID获取设备码: {}", id);
        DeviceCode deviceCode = deviceCodeRepository.findById(id)
                .filter(dc -> !GlobalConstants.Database.IS_DELETED.equals(dc.getIsDeleted()))
                .orElseThrow(() -> DeviceCodeNotFoundException.withId(id));
        
        return deviceCodeMapper.toResponseDTO(deviceCode);
    }

    /**
     * 获取所有设备码（未删除）
     *
     * @return 设备码响应DTO列表
     */
    @Transactional(readOnly = true)
    public List<DeviceCodeResponseDTO> getAllDeviceCodes() {
        log.info("获取所有设备码");
        List<DeviceCode> deviceCodes = deviceCodeRepository.findByIsDeleted(GlobalConstants.Database.NOT_DELETED);
        return deviceCodeMapper.toResponseDTOList(deviceCodes);
    }

    /**
     * 分页获取所有设备码（未删除）
     *
     * @param pageable 分页参数
     * @return 分页设备码响应DTO
     */
    @Transactional(readOnly = true)
    public Page<DeviceCodeResponseDTO> getAllDeviceCodes(Pageable pageable) {
        log.info("分页获取设备码，页码: {}, 大小: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<DeviceCode> deviceCodes = deviceCodeRepository.findAll(pageable);
        return deviceCodes.map(deviceCodeMapper::toResponseDTO);
    }

    /**
     * 更新设备码信息
     *
     * @param id        设备码ID
     * @param updateDTO 更新的设备码信息
     * @return 更新后的设备码响应DTO
     * @throws DeviceCodeNotFoundException 如果设备码不存在
     */
    public DeviceCodeResponseDTO updateDeviceCode(Long id, DeviceCodeUpdateDTO updateDTO) {
        log.info("更新设备码信息，ID: {}", id);
        
        DeviceCode existingDeviceCode = deviceCodeRepository.findById(id)
                .filter(dc -> !GlobalConstants.Database.IS_DELETED.equals(dc.getIsDeleted()))
                .orElseThrow(() -> DeviceCodeNotFoundException.withId(id));
        
        // 更新字段
        if (updateDTO.getDeviceId() != null) {
            existingDeviceCode.setDeviceId(updateDTO.getDeviceId());
        }
        if (updateDTO.getStatus() != null) {
            existingDeviceCode.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getExpiredAt() != null) {
            existingDeviceCode.setExpiredAt(updateDTO.getExpiredAt());
        }
        if (updateDTO.getBoundAt() != null) {
            existingDeviceCode.setBoundAt(updateDTO.getBoundAt());
        }
        if (updateDTO.getUpdatedBy() != null) {
            existingDeviceCode.setUpdatedBy(updateDTO.getUpdatedBy());
        }
        
        // 更新时间
        existingDeviceCode.setUpdatedAt(Instant.now());
        
        DeviceCode updatedDeviceCode = deviceCodeRepository.save(existingDeviceCode);
        log.info("设备码更新成功，ID: {}", updatedDeviceCode.getId());
        
        return deviceCodeMapper.toResponseDTO(updatedDeviceCode);
    }

    /**
     * 删除设备码（软删除）
     *
     * @param id 设备码ID
     * @throws DeviceCodeNotFoundException 如果设备码不存在
     */
    public void deleteDeviceCode(Long id) {
        log.info("删除设备码，ID: {}", id);
        
        DeviceCode deviceCode = deviceCodeRepository.findById(id)
                .filter(dc -> !GlobalConstants.Database.IS_DELETED.equals(dc.getIsDeleted()))
                .orElseThrow(() -> DeviceCodeNotFoundException.withId(id));
        
        deviceCode.setIsDeleted(GlobalConstants.Database.IS_DELETED);
        deviceCode.setUpdatedAt(Instant.now());
        
        deviceCodeRepository.save(deviceCode);
        log.info("设备码删除成功，ID: {}", id);
    }

    /**
     * 根据设备码查找设备码记录
     *
     * @param deviceCode 设备码
     * @return 设备码响应DTO
     * @throws DeviceCodeNotFoundException 如果设备码不存在
     */
    @Transactional(readOnly = true)
    public DeviceCodeResponseDTO getDeviceCodeByCode(String deviceCode) {
        log.info("根据设备码查找设备码记录: {}", deviceCode);
        DeviceCode deviceCodeEntity = deviceCodeRepository.findByDeviceCode(deviceCode)
                .filter(dc -> !GlobalConstants.Database.IS_DELETED.equals(dc.getIsDeleted()))
                .orElseThrow(() -> DeviceCodeNotFoundException.withDeviceCode(deviceCode));
        
        return deviceCodeMapper.toResponseDTO(deviceCodeEntity);
    }

    /**
     * 根据设备ID获取设备码列表
     *
     * @param deviceId 设备ID
     * @return 设备码响应DTO列表
     */
    @Transactional(readOnly = true)
    public List<DeviceCodeResponseDTO> getDeviceCodesByDeviceId(Long deviceId) {
        log.info("根据设备ID获取设备码: {}", deviceId);
        List<DeviceCode> deviceCodes = deviceCodeRepository.findByDeviceId(deviceId).stream()
                .filter(dc -> !GlobalConstants.Database.IS_DELETED.equals(dc.getIsDeleted()))
                .toList();
        return deviceCodeMapper.toResponseDTOList(deviceCodes);
    }

    /**
     * 根据状态获取设备码列表
     *
     * @param status 设备码状态
     * @return 设备码响应DTO列表
     */
    @Transactional(readOnly = true)
    public List<DeviceCodeResponseDTO> getDeviceCodesByStatus(String status) {
        log.info("根据状态获取设备码: {}", status);
        List<DeviceCode> deviceCodes = deviceCodeRepository.findByStatusAndIsDeleted(status, GlobalConstants.Database.NOT_DELETED);
        return deviceCodeMapper.toResponseDTOList(deviceCodes);
    }

    /**
     * 绑定设备码到设备
     *
     * @param deviceCode 设备码
     * @param deviceId   设备ID
     * @param boundBy    绑定操作者ID
     * @return 绑定后的设备码响应DTO
     * @throws DeviceCodeNotFoundException 如果设备码不存在
     */
    public DeviceCodeResponseDTO bindDeviceCode(String deviceCode, Long deviceId, Long boundBy) {
        log.info("绑定设备码到设备，设备码: {}, 设备ID: {}", deviceCode, deviceId);
        
        DeviceCode deviceCodeEntity = deviceCodeRepository.findByDeviceCode(deviceCode)
                .filter(dc -> !GlobalConstants.Database.IS_DELETED.equals(dc.getIsDeleted()))
                .orElseThrow(() -> DeviceCodeNotFoundException.withDeviceCode(deviceCode));
        
        // 检查设备码状态
        if (!"UNUSED".equals(deviceCodeEntity.getStatus())) {
            throw new RuntimeException("设备码状态不允许绑定，当前状态: " + deviceCodeEntity.getStatus());
        }
        
        // 检查是否过期
        if (deviceCodeEntity.getExpiredAt() != null && deviceCodeEntity.getExpiredAt().isBefore(Instant.now())) {
            throw new RuntimeException("设备码已过期");
        }
        
        // 更新绑定信息
        deviceCodeEntity.setDeviceId(deviceId);
        deviceCodeEntity.setStatus("BOUND");
        deviceCodeEntity.setBoundAt(Instant.now());
        deviceCodeEntity.setUpdatedAt(Instant.now());
        deviceCodeEntity.setUpdatedBy(boundBy);
        
        DeviceCode savedDeviceCode = deviceCodeRepository.save(deviceCodeEntity);
        log.info("设备码绑定成功，设备码: {}, 设备ID: {}", deviceCode, deviceId);
        
        return deviceCodeMapper.toResponseDTO(savedDeviceCode);
    }

    /**
     * 生成新的设备码
     *
     * @param deviceId  关联的设备ID（可选）
     * @param expiredAt 过期时间
     * @param createdBy 创建者ID
     * @return 生成的设备码响应DTO
     */
    public DeviceCodeResponseDTO generateDeviceCode(Long deviceId, Instant expiredAt, Long createdBy) {
        log.info("生成新设备码，设备ID: {}", deviceId);
        
        // 生成唯一设备码
        String generatedCode;
        do {
            generatedCode = generateUniqueCode();
        } while (deviceCodeRepository.findByDeviceCode(generatedCode).isPresent());
        
        DeviceCodeRequestDTO requestDTO = DeviceCodeRequestDTO.builder()
                .deviceCode(generatedCode)
                .deviceId(deviceId)
                .status("UNUSED")
                .expiredAt(expiredAt)
                .createdBy(createdBy)
                .build();
        
        return createDeviceCode(requestDTO);
    }

    /**
     * 获取未使用的设备码列表
     *
     * @return 未使用的设备码响应DTO列表
     */
    @Transactional(readOnly = true)
    public List<DeviceCodeResponseDTO> getUnusedDeviceCodes() {
        log.info("获取未使用的设备码列表");
        List<DeviceCode> deviceCodes = deviceCodeRepository.findUnusedDeviceCodes();
        return deviceCodeMapper.toResponseDTOList(deviceCodes);
    }

    /**
     * 获取已过期的设备码列表
     *
     * @return 已过期的设备码响应DTO列表
     */
    @Transactional(readOnly = true)
    public List<DeviceCodeResponseDTO> getExpiredDeviceCodes() {
        log.info("获取已过期的设备码列表");
        List<DeviceCode> deviceCodes = deviceCodeRepository.findExpiredDeviceCodes(Instant.now());
        return deviceCodeMapper.toResponseDTOList(deviceCodes);
    }

    /**
     * 批量过期设备码
     *
     * @param expiredBy 过期操作者ID
     * @return 过期的设备码数量
     */
    public int expireDeviceCodes(Long expiredBy) {
        log.info("批量过期设备码");
        List<DeviceCode> expiredCodes = deviceCodeRepository.findExpiredDeviceCodes(Instant.now());
        
        int count = 0;
        for (DeviceCode deviceCode : expiredCodes) {
            if (!"EXPIRED".equals(deviceCode.getStatus())) {
                deviceCode.setStatus("EXPIRED");
                deviceCode.setUpdatedAt(Instant.now());
                deviceCode.setUpdatedBy(expiredBy);
                deviceCodeRepository.save(deviceCode);
                count++;
            }
        }
        
        log.info("批量过期设备码完成，过期数量: {}", count);
        return count;
    }

    /**
     * 生成唯一设备码
     *
     * @return 唯一设备码
     */
    private String generateUniqueCode() {
        return "DC" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
