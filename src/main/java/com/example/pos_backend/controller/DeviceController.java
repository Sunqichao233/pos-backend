package com.example.pos_backend.controller;

import com.example.pos_backend.common.ApiResponse;
import com.example.pos_backend.constants.GlobalConstants;
import com.example.pos_backend.dto.*;
import com.example.pos_backend.entity.Device;
import com.example.pos_backend.exception.DeviceNotFoundException;
import com.example.pos_backend.service.DeviceService;
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
import java.util.Optional;

/**
 * 设备控制器
 * 提供设备相关的REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final DeviceMapper deviceMapper;

    /**
     * 创建新设备
     *
     * @param deviceRequestDTO 设备创建信息
     * @return 创建的设备信息
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DeviceResponseDTO>> createDevice(@Valid @RequestBody DeviceRequestDTO deviceRequestDTO) {
        log.info("接收创建设备请求: {}", deviceRequestDTO.getDeviceName());
        try {
            Device device = deviceMapper.toEntity(deviceRequestDTO);
            Device createdDevice = deviceService.createDevice(device);
            DeviceResponseDTO responseDTO = deviceMapper.toResponseDTO(createdDevice);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(responseDTO));
        } catch (Exception e) {
            log.error("创建设备失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.badRequest("创建设备失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取设备
     *
     * @param id 设备ID
     * @return 设备信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceResponseDTO>> getDeviceById(@PathVariable Long id) {
        log.info("接收获取设备请求，ID: {}", id);
        try {
            Device device = deviceService.getDeviceById(id);
            DeviceResponseDTO responseDTO = deviceMapper.toResponseDTO(device);
            return ResponseEntity.ok(ApiResponse.success(responseDTO));
        } catch (DeviceNotFoundException e) {
            log.error("获取设备失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound(e.getMessage()));
        }
    }

    /**
     * 获取所有设备（支持分页）
     *
     * @param pageable    分页参数（可选）
     * @param enablePaging 是否启用分页
     * @return 设备列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllDevices(
            @PageableDefault(size = GlobalConstants.Database.DEFAULT_PAGE_SIZE) Pageable pageable,
            @RequestParam(value = "page", defaultValue = "false") boolean enablePaging) {
        log.info("接收获取所有设备请求，分页: {}", enablePaging);
        
        try {
            if (enablePaging) {
                Page<Device> devices = deviceService.getAllDevices(pageable);
                Page<DeviceResponseDTO> responseDTOs = devices.map(deviceMapper::toResponseDTO);
                return ResponseEntity.ok(ApiResponse.success(responseDTOs));
            } else {
                List<Device> devices = deviceService.getAllDevices();
                List<DeviceResponseDTO> responseDTOs = deviceMapper.toResponseDTOList(devices);
                return ResponseEntity.ok(ApiResponse.success(responseDTOs));
            }
        } catch (Exception e) {
            log.error("获取设备列表失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("获取设备列表失败"));
        }
    }

    /**
     * 更新设备信息
     *
     * @param id              设备ID
     * @param deviceUpdateDTO 更新的设备信息
     * @return 更新后的设备信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceResponseDTO>> updateDevice(@PathVariable Long id, @Valid @RequestBody DeviceUpdateDTO deviceUpdateDTO) {
        log.info("接收更新设备请求，ID: {}", id);
        try {
            Device updateDevice = deviceMapper.toEntityFromUpdateDTO(deviceUpdateDTO);
            Device updatedDevice = deviceService.updateDevice(id, updateDevice);
            DeviceResponseDTO responseDTO = deviceMapper.toResponseDTO(updatedDevice);
            return ResponseEntity.ok(ApiResponse.updated(responseDTO));
        } catch (DeviceNotFoundException e) {
            log.error("更新设备失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound(e.getMessage()));
        } catch (Exception e) {
            log.error("更新设备失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.badRequest("更新设备失败: " + e.getMessage()));
        }
    }

    /**
     * 删除设备（软删除）
     *
     * @param id 设备ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDevice(@PathVariable Long id) {
        log.info("接收删除设备请求，ID: {}", id);
        try {
            deviceService.deleteDevice(id);
            return ResponseEntity.ok(ApiResponse.deleted());
        } catch (DeviceNotFoundException e) {
            log.error("删除设备失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound(e.getMessage()));
        } catch (Exception e) {
            log.error("删除设备失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("删除设备失败"));
        }
    }

    /**
     * 根据设备名称查找设备
     *
     * @param deviceName 设备名称
     * @return 设备信息
     */
    @GetMapping("/name/{deviceName}")
    public ResponseEntity<ApiResponse<DeviceResponseDTO>> getDeviceByName(@PathVariable String deviceName) {
        log.info("接收根据设备名称获取设备请求: {}", deviceName);
        Optional<Device> device = deviceService.findByDeviceName(deviceName);
        return device.map(d -> ResponseEntity.ok(ApiResponse.success(deviceMapper.toResponseDTO(d))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("设备不存在")));
    }

    /**
     * 根据设备类型获取设备列表
     *
     * @param deviceType 设备类型
     * @return 设备列表
     */
    @GetMapping("/type/{deviceType}")
    public ResponseEntity<ApiResponse<List<DeviceResponseDTO>>> getDevicesByType(@PathVariable String deviceType) {
        log.info("接收根据设备类型获取设备请求: {}", deviceType);
        try {
            List<Device> devices = deviceService.findByDeviceType(deviceType);
            List<DeviceResponseDTO> responseDTOs = deviceMapper.toResponseDTOList(devices);
            return ResponseEntity.ok(ApiResponse.success(responseDTOs));
        } catch (Exception e) {
            log.error("根据设备类型获取设备失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("获取设备失败"));
        }
    }

    /**
     * 根据状态获取设备列表
     *
     * @param status 设备状态
     * @return 设备列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<DeviceResponseDTO>>> getDevicesByStatus(@PathVariable String status) {
        log.info("接收根据状态获取设备请求: {}", status);
        try {
            List<Device> devices = deviceService.findByStatus(status);
            List<DeviceResponseDTO> responseDTOs = deviceMapper.toResponseDTOList(devices);
            return ResponseEntity.ok(ApiResponse.success(responseDTOs));
        } catch (Exception e) {
            log.error("根据状态获取设备失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("获取设备失败"));
        }
    }

    /**
     * 根据MAC地址查找设备
     *
     * @param macAddress MAC地址
     * @return 设备信息
     */
    @GetMapping("/mac/{macAddress}")
    public ResponseEntity<ApiResponse<DeviceResponseDTO>> getDeviceByMacAddress(@PathVariable String macAddress) {
        log.info("接收根据MAC地址获取设备请求: {}", macAddress);
        Optional<Device> device = deviceService.findByMacAddress(macAddress);
        return device.map(d -> ResponseEntity.ok(ApiResponse.success(deviceMapper.toResponseDTO(d))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("设备不存在")));
    }

    /**
     * 根据IP地址查找设备
     *
     * @param ipAddress IP地址
     * @return 设备信息
     */
    @GetMapping("/ip/{ipAddress}")
    public ResponseEntity<ApiResponse<DeviceResponseDTO>> getDeviceByIpAddress(@PathVariable String ipAddress) {
        log.info("接收根据IP地址获取设备请求: {}", ipAddress);
        Optional<Device> device = deviceService.findByIpAddress(ipAddress);
        return device.map(d -> ResponseEntity.ok(ApiResponse.success(deviceMapper.toResponseDTO(d))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("设备不存在")));
    }

    /**
     * 搜索设备（根据设备名称模糊查询）
     *
     * @param keyword 搜索关键字
     * @return 设备列表
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DeviceResponseDTO>>> searchDevices(@RequestParam String keyword) {
        log.info("接收搜索设备请求，关键字: {}", keyword);
        try {
            List<Device> devices = deviceService.searchDevices(keyword);
            List<DeviceResponseDTO> responseDTOs = deviceMapper.toResponseDTOList(devices);
            return ResponseEntity.ok(ApiResponse.success(responseDTOs));
        } catch (Exception e) {
            log.error("搜索设备失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("搜索设备失败"));
        }
    }

    /**
     * 获取在线设备列表
     *
     * @return 在线设备列表
     */
    @GetMapping("/online")
    public ResponseEntity<ApiResponse<List<DeviceResponseDTO>>> getOnlineDevices() {
        log.info("接收获取在线设备请求");
        try {
            List<Device> devices = deviceService.getOnlineDevices();
            List<DeviceResponseDTO> responseDTOs = deviceMapper.toResponseDTOList(devices);
            return ResponseEntity.ok(ApiResponse.success(responseDTOs));
        } catch (Exception e) {
            log.error("获取在线设备失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("获取在线设备失败"));
        }
    }

    /**
     * 获取离线设备列表
     *
     * @return 离线设备列表
     */
    @GetMapping("/offline")
    public ResponseEntity<ApiResponse<List<DeviceResponseDTO>>> getOfflineDevices() {
        log.info("接收获取离线设备请求");
        try {
            List<Device> devices = deviceService.getOfflineDevices();
            List<DeviceResponseDTO> responseDTOs = deviceMapper.toResponseDTOList(devices);
            return ResponseEntity.ok(ApiResponse.success(responseDTOs));
        } catch (Exception e) {
            log.error("获取离线设备失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("获取离线设备失败"));
        }
    }

    /**
     * 更新设备状态
     *
     * @param id     设备ID
     * @param status 新状态
     * @return 更新后的设备信息
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DeviceResponseDTO>> updateDeviceStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("接收更新设备状态请求，ID: {}, 状态: {}", id, status);
        try {
            Device updatedDevice = deviceService.updateDeviceStatus(id, status);
            DeviceResponseDTO responseDTO = deviceMapper.toResponseDTO(updatedDevice);
            return ResponseEntity.ok(ApiResponse.updated(responseDTO));
        } catch (DeviceNotFoundException e) {
            log.error("更新设备状态失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound(e.getMessage()));
        } catch (Exception e) {
            log.error("更新设备状态失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("更新设备状态失败"));
        }
    }

    /**
     * 更新设备最后在线时间
     *
     * @param id 设备ID
     * @return 更新结果
     */
    @PatchMapping("/{id}/online")
    public ResponseEntity<ApiResponse<Void>> updateLastOnlineTime(@PathVariable Long id) {
        log.info("接收更新设备在线时间请求，ID: {}", id);
        try {
            deviceService.updateLastOnlineTime(id);
            return ResponseEntity.ok(ApiResponse.success("设备在线时间更新成功"));
        } catch (DeviceNotFoundException e) {
            log.error("更新设备在线时间失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound(e.getMessage()));
        } catch (Exception e) {
            log.error("更新设备在线时间失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("更新设备在线时间失败"));
        }
    }

    /**
     * 根据创建者获取设备列表
     *
     * @param createdBy 创建者ID
     * @return 设备列表
     */
    @GetMapping("/creator/{createdBy}")
    public ResponseEntity<ApiResponse<List<DeviceResponseDTO>>> getDevicesByCreator(@PathVariable Long createdBy) {
        log.info("接收根据创建者获取设备请求: {}", createdBy);
        try {
            List<Device> devices = deviceService.findByCreatedBy(createdBy);
            List<DeviceResponseDTO> responseDTOs = deviceMapper.toResponseDTOList(devices);
            return ResponseEntity.ok(ApiResponse.success(responseDTOs));
        } catch (Exception e) {
            log.error("根据创建者获取设备失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("获取设备失败"));
        }
    }

    /**
     * 获取设备统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDeviceStats() {
        log.info("接收获取设备统计信息请求");
        try {
            List<Device> allDevices = deviceService.getAllDevices();
            long totalDevices = allDevices.size();
            long onlineDevices = deviceService.countByStatus("ONLINE");
            long offlineDevices = deviceService.countByStatus("OFFLINE");

            Map<String, Object> stats = Map.of(
                    "totalDevices", totalDevices,
                    "onlineDevices", onlineDevices,
                    "offlineDevices", offlineDevices
            );

            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            log.error("获取设备统计信息失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("获取设备统计信息失败"));
        }
    }

    /**
     * 查找长时间未在线的设备
     *
     * @param hours 小时数（默认24小时）
     * @return 设备列表
     */
    @GetMapping("/inactive")
    public ResponseEntity<ApiResponse<List<DeviceResponseDTO>>> getInactiveDevices(@RequestParam(defaultValue = "24") int hours) {
        log.info("接收获取长时间未在线设备请求，小时数: {}", hours);
        try {
            Instant threshold = Instant.now().minusSeconds(hours * 3600L);
            List<Device> devices = deviceService.findDevicesNotOnlineSince(threshold);
            List<DeviceResponseDTO> responseDTOs = deviceMapper.toResponseDTOList(devices);
            return ResponseEntity.ok(ApiResponse.success(responseDTOs));
        } catch (Exception e) {
            log.error("获取长时间未在线设备失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("获取设备失败"));
        }
    }
}
