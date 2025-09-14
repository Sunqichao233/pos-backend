package com.example.pos_backend.service;

import com.example.pos_backend.constants.GlobalConstants;
import com.example.pos_backend.dto.*;
import com.example.pos_backend.entity.DeviceCode;
import com.example.pos_backend.exception.DeviceCodeNotFoundException;
import com.example.pos_backend.repository.DeviceCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * 设备激活码服务类 - Square风格
 * 提供设备激活码相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeviceCodeService {

    private final DeviceCodeRepository deviceCodeRepository;
    private final DeviceCodeMapper deviceCodeMapper;
    private static final String ACTIVATION_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 为指定设备生成激活码（Square风格：按需生成）
     *
     * @param deviceId  设备ID
     * @param createdBy 创建者ID
     * @return 生成的激活码响应DTO
     */
    public DeviceCodeResponseDTO generateActivationCodeForDevice(Long deviceId, Long createdBy) {
        log.info("为设备生成激活码，设备ID: {}", deviceId);
        
        // 1. 检查设备是否已有有效激活码，如有则使其过期
        Optional<DeviceCode> existingCode = deviceCodeRepository.findActiveDeviceCodeByDeviceId(deviceId);
        if (existingCode.isPresent()) {
            log.info("设备已有有效激活码，将其设为过期，设备ID: {}", deviceId);
            DeviceCode oldCode = existingCode.get();
            oldCode.setStatus("EXPIRED");
            oldCode.setUpdatedAt(Instant.now());
            deviceCodeRepository.save(oldCode);
        }
        
        // 2. 生成新的唯一激活码
        String activationCode = generateUniqueActivationCode();
        
        // 3. 创建新的激活码记录
        DeviceCode deviceCode = DeviceCode.builder()
                .deviceCode(activationCode)
                .deviceId(deviceId)
                .status("UNUSED")
                .activationAttempts(0)
                .maxAttempts(3)
                .issuedAt(Instant.now())
                .expiredAt(Instant.now().plus(24, ChronoUnit.HOURS)) // 24小时过期
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy(createdBy)
                .isDeleted(GlobalConstants.Database.NOT_DELETED)
                .build();
        
        DeviceCode savedDeviceCode = deviceCodeRepository.save(deviceCode);
        log.info("激活码生成成功，设备ID: {}, 激活码: {}", deviceId, activationCode);
        
        return deviceCodeMapper.toResponseDTO(savedDeviceCode);
    }

    /**
     * 用户自助激活设备（Square风格核心功能）
     *
     * @param request 激活请求
     * @return 激活结果
     */
    public DeviceCodeResponseDTO activateDevice(DeviceActivationRequestDTO request) {
        log.info("用户尝试激活设备，激活码: {}", request.getActivationCode());
        
        // 1. 查找激活码
        DeviceCode deviceCode = deviceCodeRepository.findByDeviceCode(request.getActivationCode())
                .filter(dc -> !GlobalConstants.Database.IS_DELETED.equals(dc.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("激活码不存在或已失效"));
        
        // 2. 检查激活码状态
        if (!"UNUSED".equals(deviceCode.getStatus())) {
            throw new RuntimeException("激活码已被使用或已过期");
        }
        
        // 3. 检查是否过期
        if (deviceCode.getExpiredAt() != null && deviceCode.getExpiredAt().isBefore(Instant.now())) {
            deviceCode.setStatus("EXPIRED");
            deviceCode.setUpdatedAt(Instant.now());
            deviceCodeRepository.save(deviceCode);
            throw new RuntimeException("激活码已过期");
        }
        
        // 4. 检查激活尝试次数
        if (deviceCode.getActivationAttempts() >= deviceCode.getMaxAttempts()) {
            deviceCode.setStatus("EXPIRED");
            deviceCode.setUpdatedAt(Instant.now());
            deviceCodeRepository.save(deviceCode);
            throw new RuntimeException("激活尝试次数已达上限，激活码已失效");
        }
        
        // 5. 检查设备指纹是否已被其他设备绑定
        Optional<DeviceCode> boundCode = deviceCodeRepository.findBoundDeviceCodeByFingerprint(request.getDeviceFingerprint());
        if (boundCode.isPresent()) {
            // 增加尝试次数
            deviceCode.setActivationAttempts(deviceCode.getActivationAttempts() + 1);
            deviceCode.setUpdatedAt(Instant.now());
            deviceCodeRepository.save(deviceCode);
            throw new RuntimeException("该设备已绑定其他激活码");
        }
        
        try {
            // 6. 激活成功 - 更新激活码状态
            deviceCode.setStatus("BOUND");
            deviceCode.setDeviceFingerprint(request.getDeviceFingerprint());
            deviceCode.setBoundAt(Instant.now());
            deviceCode.setUpdatedAt(Instant.now());
            
            DeviceCode activatedCode = deviceCodeRepository.save(deviceCode);
            log.info("设备激活成功，设备ID: {}, 激活码: {}", deviceCode.getDeviceId(), request.getActivationCode());
            
            return deviceCodeMapper.toResponseDTO(activatedCode);
            
        } catch (Exception e) {
            // 7. 激活失败 - 增加尝试次数
            deviceCode.setActivationAttempts(deviceCode.getActivationAttempts() + 1);
            deviceCode.setUpdatedAt(Instant.now());
            deviceCodeRepository.save(deviceCode);
            
            log.error("设备激活失败，激活码: {}, 错误: {}", request.getActivationCode(), e.getMessage());
            throw new RuntimeException("激活失败: " + e.getMessage());
        }
    }

    /**
     * 根据激活码查询状态
     *
     * @param activationCode 激活码
     * @return 激活码信息
     */
    @Transactional(readOnly = true)
    public DeviceCodeResponseDTO getActivationCodeStatus(String activationCode) {
        log.info("查询激活码状态: {}", activationCode);
        DeviceCode deviceCode = deviceCodeRepository.findByDeviceCode(activationCode)
                .filter(dc -> !GlobalConstants.Database.IS_DELETED.equals(dc.getIsDeleted()))
                .orElseThrow(() -> DeviceCodeNotFoundException.withDeviceCode(activationCode));
        
        return deviceCodeMapper.toResponseDTO(deviceCode);
    }

    /**
     * 根据设备指纹查找已绑定的激活码
     *
     * @param deviceFingerprint 设备指纹
     * @return 绑定的激活码信息
     */
    @Transactional(readOnly = true)
    public Optional<DeviceCodeResponseDTO> findBoundCodeByFingerprint(String deviceFingerprint) {
        log.info("根据设备指纹查找绑定的激活码: {}", deviceFingerprint);
        return deviceCodeRepository.findBoundDeviceCodeByFingerprint(deviceFingerprint)
                .map(deviceCodeMapper::toResponseDTO);
    }

    /**
     * 检查设备是否有有效激活码
     *
     * @param deviceId 设备ID
     * @return 是否有有效激活码
     */
    @Transactional(readOnly = true)
    public boolean hasActiveActivationCode(Long deviceId) {
        log.info("检查设备是否有有效激活码，设备ID: {}", deviceId);
        return deviceCodeRepository.findActiveDeviceCodeByDeviceId(deviceId).isPresent();
    }

    /**
     * 使设备的所有激活码失效（设备重置时使用）
     *
     * @param deviceId 设备ID
     * @return 失效的激活码数量
     */
    public int invalidateDeviceActivationCodes(Long deviceId) {
        log.info("使设备所有激活码失效，设备ID: {}", deviceId);
        
        var deviceCodes = deviceCodeRepository.findLatestDeviceCodesByDeviceId(deviceId);
        int count = 0;
        
        for (DeviceCode code : deviceCodes) {
            if ("UNUSED".equals(code.getStatus()) || "BOUND".equals(code.getStatus())) {
                code.setStatus("EXPIRED");
                code.setUpdatedAt(Instant.now());
                deviceCodeRepository.save(code);
                count++;
            }
        }
        
        log.info("设备激活码失效完成，设备ID: {}, 失效数量: {}", deviceId, count);
        return count;
    }

    /**
     * 批量清理过期的激活码
     *
     * @return 清理的激活码数量
     */
    public int cleanupExpiredActivationCodes() {
        log.info("开始批量清理过期激活码");
        Instant now = Instant.now();
        
        // 使用批量更新提高性能
        int count = deviceCodeRepository.batchExpireDeviceCodes(now);
        
        log.info("过期激活码清理完成，清理数量: {}", count);
        return count;
    }

    /**
     * 生成唯一的12位激活码
     *
     * @return 12位数字字母混合激活码
     */
    private String generateUniqueActivationCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (deviceCodeRepository.findByDeviceCode(code).isPresent());
        
        return code;
    }

    /**
     * 生成随机12位激活码
     *
     * @return 12位激活码
     */
    private String generateRandomCode() {
        StringBuilder code = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            code.append(ACTIVATION_CODE_CHARS.charAt(RANDOM.nextInt(ACTIVATION_CODE_CHARS.length())));
        }
        return code.toString();
    }
}