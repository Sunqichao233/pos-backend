package com.example.pos_backend.controller;

import com.example.pos_backend.common.ApiResponse;
import com.example.pos_backend.dto.DeviceActivationRequestDTO;
import com.example.pos_backend.dto.DeviceCodeResponseDTO;
import com.example.pos_backend.exception.DeviceCodeNotFoundException;
import com.example.pos_backend.service.DeviceCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * 设备激活码控制器 - Square风格简化API
 * 提供设备激活相关的REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/activation")
@RequiredArgsConstructor
public class DeviceCodeController {

    private final DeviceCodeService deviceCodeService;

    /**
     * 为指定设备生成激活码
     *
     * @param deviceId  设备ID
     * @param createdBy 创建者ID（可选）
     * @return 生成的激活码信息
     */
    @PostMapping("/generate/{deviceId}")
    public ResponseEntity<ApiResponse<DeviceCodeResponseDTO>> generateActivationCode(
            @PathVariable Long deviceId,
            @RequestParam(required = false) Long createdBy) {
        log.info("接收生成激活码请求，设备ID: {}", deviceId);
        try {
            DeviceCodeResponseDTO responseDTO = deviceCodeService.generateActivationCodeForDevice(deviceId, createdBy);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(responseDTO));
        } catch (Exception e) {
            log.error("生成激活码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("生成激活码失败: " + e.getMessage()));
        }
    }

    /**
     * 用户自助激活设备（核心功能）
     *
     * @param request 激活请求
     * @return 激活结果
     */
    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<DeviceCodeResponseDTO>> activateDevice(@Valid @RequestBody DeviceActivationRequestDTO request) {
        log.info("接收设备激活请求，激活码: {}", request.getActivationCode());
        try {
            DeviceCodeResponseDTO responseDTO = deviceCodeService.activateDevice(request);
            return ResponseEntity.ok(ApiResponse.success(responseDTO, "设备激活成功"));
        } catch (RuntimeException e) {
            log.error("设备激活失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("激活失败: " + e.getMessage()));
        } catch (Exception e) {
            log.error("设备激活异常: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("激活异常，请稍后重试"));
        }
    }

    /**
     * 查询激活码状态
     *
     * @param activationCode 激活码
     * @return 激活码状态信息
     */
    @GetMapping("/status/{activationCode}")
    public ResponseEntity<ApiResponse<DeviceCodeResponseDTO>> getActivationCodeStatus(@PathVariable String activationCode) {
        log.info("接收查询激活码状态请求: {}", activationCode);
        try {
            DeviceCodeResponseDTO responseDTO = deviceCodeService.getActivationCodeStatus(activationCode);
            return ResponseEntity.ok(ApiResponse.success(responseDTO));
        } catch (DeviceCodeNotFoundException e) {
            log.error("激活码不存在: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        } catch (Exception e) {
            log.error("查询激活码状态失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查询失败"));
        }
    }

    /**
     * 根据设备指纹查找已绑定的激活码
     *
     * @param fingerprint 设备指纹
     * @return 绑定的激活码信息
     */
    @GetMapping("/fingerprint/{fingerprint}")
    public ResponseEntity<ApiResponse<DeviceCodeResponseDTO>> getBoundCodeByFingerprint(@PathVariable String fingerprint) {
        log.info("接收根据设备指纹查询请求: {}", fingerprint);
        try {
            Optional<DeviceCodeResponseDTO> responseDTO = deviceCodeService.findBoundCodeByFingerprint(fingerprint);
            if (responseDTO.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(responseDTO.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("该设备指纹未绑定激活码"));
            }
        } catch (Exception e) {
            log.error("查询设备指纹绑定失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查询失败"));
        }
    }

    /**
     * 检查设备是否有有效激活码
     *
     * @param deviceId 设备ID
     * @return 检查结果
     */
    @GetMapping("/check/{deviceId}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkDeviceActivationStatus(@PathVariable Long deviceId) {
        log.info("接收检查设备激活状态请求，设备ID: {}", deviceId);
        try {
            boolean hasActiveCode = deviceCodeService.hasActiveActivationCode(deviceId);
            Map<String, Boolean> result = Map.of("hasActiveCode", hasActiveCode);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("检查设备激活状态失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("检查失败"));
        }
    }

    /**
     * 使设备的所有激活码失效（设备重置时使用）
     *
     * @param deviceId 设备ID
     * @return 失效结果
     */
    @PostMapping("/invalidate/{deviceId}")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> invalidateDeviceActivationCodes(@PathVariable Long deviceId) {
        log.info("接收使设备激活码失效请求，设备ID: {}", deviceId);
        try {
            int invalidatedCount = deviceCodeService.invalidateDeviceActivationCodes(deviceId);
            Map<String, Integer> result = Map.of("invalidatedCount", invalidatedCount);
            return ResponseEntity.ok(ApiResponse.success(result, "激活码失效完成"));
        } catch (Exception e) {
            log.error("使激活码失效失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("操作失败"));
        }
    }

    /**
     * 批量清理过期激活码（管理员功能）
     *
     * @return 清理结果
     */
    @PostMapping("/cleanup")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> cleanupExpiredActivationCodes() {
        log.info("接收清理过期激活码请求");
        try {
            int cleanedCount = deviceCodeService.cleanupExpiredActivationCodes();
            Map<String, Integer> result = Map.of("cleanedCount", cleanedCount);
            return ResponseEntity.ok(ApiResponse.success(result, "过期激活码清理完成"));
        } catch (Exception e) {
            log.error("清理过期激活码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("清理失败"));
        }
    }
}