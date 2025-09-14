package com.example.pos_backend.controller;

import com.example.pos_backend.common.ApiResponse;
import com.example.pos_backend.constants.GlobalConstants;
import com.example.pos_backend.dto.DeviceCodeRequestDTO;
import com.example.pos_backend.dto.DeviceCodeResponseDTO;
import com.example.pos_backend.dto.DeviceCodeUpdateDTO;
import com.example.pos_backend.exception.DeviceCodeNotFoundException;
import com.example.pos_backend.service.DeviceCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 设备码控制器
 * 提供设备码相关的REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/device-codes")
@RequiredArgsConstructor
public class DeviceCodeController {

    private final DeviceCodeService deviceCodeService;

    /**
     * 创建新设备码
     *
     * @param requestDTO 设备码创建信息
     * @return 创建的设备码信息
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DeviceCodeResponseDTO>> createDeviceCode(@Valid @RequestBody DeviceCodeRequestDTO requestDTO) {
        log.info("接收创建设备码请求: {}", requestDTO.getDeviceCode());
        try {
            DeviceCodeResponseDTO responseDTO = deviceCodeService.createDeviceCode(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(responseDTO));
        } catch (Exception e) {
            log.error("创建设备码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.badRequest("创建设备码失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取设备码
     *
     * @param id 设备码ID
     * @return 设备码信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceCodeResponseDTO>> getDeviceCodeById(@PathVariable Long id) {
        log.info("接收获取设备码请求，ID: {}", id);
        try {
            DeviceCodeResponseDTO responseDTO = deviceCodeService.getDeviceCodeById(id);
            return ResponseEntity.ok(ApiResponse.success(responseDTO));
        } catch (DeviceCodeNotFoundException e) {
            log.error("获取设备码失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound(e.getMessage()));
        }
    }

    /**
     * 获取所有设备码（支持分页）
     *
     * @param pageable     分页参数（可选）
     * @param enablePaging 是否启用分页
     * @return 设备码列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllDeviceCodes(
            @PageableDefault(size = GlobalConstants.Database.DEFAULT_PAGE_SIZE) Pageable pageable,
            @RequestParam(value = "page", defaultValue = "false") boolean enablePaging) {
        log.info("接收获取所有设备码请求，分页: {}", enablePaging);
        
        try {
            if (enablePaging) {
                Page<DeviceCodeResponseDTO> responseDTOs = deviceCodeService.getAllDeviceCodes(pageable);
                return ResponseEntity.ok(ApiResponse.success(responseDTOs));
            } else {
                List<DeviceCodeResponseDTO> responseDTOs = deviceCodeService.getAllDeviceCodes();
                return ResponseEntity.ok(ApiResponse.success(responseDTOs));
            }
        } catch (Exception e) {
            log.error("获取设备码列表失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("获取设备码列表失败"));
        }
    }

    /**
     * 更新设备码信息
     *
     * @param id        设备码ID
     * @param updateDTO 更新的设备码信息
     * @return 更新后的设备码信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceCodeResponseDTO>> updateDeviceCode(@PathVariable Long id, @Valid @RequestBody DeviceCodeUpdateDTO updateDTO) {
        log.info("接收更新设备码请求，ID: {}", id);
        try {
            DeviceCodeResponseDTO responseDTO = deviceCodeService.updateDeviceCode(id, updateDTO);
            return ResponseEntity.ok(ApiResponse.updated(responseDTO));
        } catch (DeviceCodeNotFoundException e) {
            log.error("更新设备码失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound(e.getMessage()));
        } catch (Exception e) {
            log.error("更新设备码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.badRequest("更新设备码失败: " + e.getMessage()));
        }
    }

    /**
     * 删除设备码（软删除）
     *
     * @param id 设备码ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeviceCode(@PathVariable Long id) {
        log.info("接收删除设备码请求，ID: {}", id);
        try {
            deviceCodeService.deleteDeviceCode(id);
            return ResponseEntity.noContent().build();
        } catch (DeviceCodeNotFoundException e) {
            log.error("删除设备码失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("删除设备码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据设备码查找设备码记录
     *
     * @param deviceCode 设备码
     * @return 设备码信息
     */
    @GetMapping("/code/{deviceCode}")
    public ResponseEntity<ApiResponse<DeviceCodeResponseDTO>> getDeviceCodeByCode(@PathVariable String deviceCode) {
        log.info("接收根据设备码获取设备码记录请求: {}", deviceCode);
        try {
            DeviceCodeResponseDTO responseDTO = deviceCodeService.getDeviceCodeByCode(deviceCode);
            return ResponseEntity.ok(ApiResponse.success(responseDTO));
        } catch (DeviceCodeNotFoundException e) {
            log.error("获取设备码记录失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound(e.getMessage()));
        }
    }

    /**
     * 根据设备ID获取设备码列表
     *
     * @param deviceId 设备ID
     * @return 设备码列表
     */
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<ApiResponse<List<DeviceCodeResponseDTO>>> getDeviceCodesByDeviceId(@PathVariable Long deviceId) {
        log.info("接收根据设备ID获取设备码请求: {}", deviceId);
        try {
            List<DeviceCodeResponseDTO> responseDTOs = deviceCodeService.getDeviceCodesByDeviceId(deviceId);
            return ResponseEntity.ok(ApiResponse.success(responseDTOs));
        } catch (Exception e) {
            log.error("根据设备ID获取设备码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("获取设备码失败"));
        }
    }

    /**
     * 根据状态获取设备码列表
     *
     * @param status 设备码状态
     * @return 设备码列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<DeviceCodeResponseDTO>>> getDeviceCodesByStatus(@PathVariable String status) {
        log.info("接收根据状态获取设备码请求: {}", status);
        try {
            List<DeviceCodeResponseDTO> responseDTOs = deviceCodeService.getDeviceCodesByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(responseDTOs));
        } catch (Exception e) {
            log.error("根据状态获取设备码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("获取设备码失败"));
        }
    }

    /**
     * 绑定设备码到设备
     *
     * @param deviceCode 设备码
     * @param deviceId   设备ID
     * @param boundBy    绑定操作者ID（可选）
     * @return 绑定后的设备码信息
     */
    @PostMapping("/{deviceCode}/bind")
    public ResponseEntity<ApiResponse<DeviceCodeResponseDTO>> bindDeviceCode(
            @PathVariable String deviceCode,
            @RequestParam Long deviceId,
            @RequestParam(required = false) Long boundBy) {
        log.info("接收绑定设备码请求，设备码: {}, 设备ID: {}", deviceCode, deviceId);
        try {
            DeviceCodeResponseDTO responseDTO = deviceCodeService.bindDeviceCode(deviceCode, deviceId, boundBy);
            return ResponseEntity.ok(ApiResponse.success(responseDTO, "设备码绑定成功"));
        } catch (DeviceCodeNotFoundException e) {
            log.error("绑定设备码失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound(e.getMessage()));
        } catch (Exception e) {
            log.error("绑定设备码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.badRequest("绑定设备码失败: " + e.getMessage()));
        }
    }

    /**
     * 生成新的设备码
     *
     * @param deviceId  关联的设备ID（可选）
     * @param expiredAt 过期时间（可选）
     * @param createdBy 创建者ID（可选）
     * @return 生成的设备码信息
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<DeviceCodeResponseDTO>> generateDeviceCode(
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) Instant expiredAt,
            @RequestParam(required = false) Long createdBy) {
        log.info("接收生成设备码请求，设备ID: {}", deviceId);
        try {
            DeviceCodeResponseDTO responseDTO = deviceCodeService.generateDeviceCode(deviceId, expiredAt, createdBy);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(responseDTO));
        } catch (Exception e) {
            log.error("生成设备码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("生成设备码失败"));
        }
    }

    /**
     * 获取未使用的设备码列表
     *
     * @return 未使用的设备码列表
     */
    @GetMapping("/unused")
    public ResponseEntity<ApiResponse<List<DeviceCodeResponseDTO>>> getUnusedDeviceCodes() {
        log.info("接收获取未使用设备码请求");
        try {
            List<DeviceCodeResponseDTO> responseDTOs = deviceCodeService.getUnusedDeviceCodes();
            return ResponseEntity.ok(ApiResponse.success(responseDTOs));
        } catch (Exception e) {
            log.error("获取未使用设备码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("获取未使用设备码失败"));
        }
    }

    /**
     * 获取已过期的设备码列表
     *
     * @return 已过期的设备码列表
     */
    @GetMapping("/expired")
    public ResponseEntity<ApiResponse<List<DeviceCodeResponseDTO>>> getExpiredDeviceCodes() {
        log.info("接收获取已过期设备码请求");
        try {
            List<DeviceCodeResponseDTO> responseDTOs = deviceCodeService.getExpiredDeviceCodes();
            return ResponseEntity.ok(ApiResponse.success(responseDTOs));
        } catch (Exception e) {
            log.error("获取已过期设备码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("获取已过期设备码失败"));
        }
    }

    /**
     * 批量过期设备码
     *
     * @param expiredBy 过期操作者ID（可选）
     * @return 过期的设备码数量
     */
    @PostMapping("/expire")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> expireDeviceCodes(@RequestParam(required = false) Long expiredBy) {
        log.info("接收批量过期设备码请求");
        try {
            int expiredCount = deviceCodeService.expireDeviceCodes(expiredBy);
            Map<String, Integer> result = Map.of("expiredCount", expiredCount);
            return ResponseEntity.ok(ApiResponse.success(result, "批量过期设备码完成"));
        } catch (Exception e) {
            log.error("批量过期设备码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("批量过期设备码失败"));
        }
    }
}
