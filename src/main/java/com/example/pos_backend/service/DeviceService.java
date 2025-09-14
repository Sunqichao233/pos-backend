package com.example.pos_backend.service;

import com.example.pos_backend.constants.GlobalConstants;
import com.example.pos_backend.entity.Device;
import com.example.pos_backend.exception.DeviceNotFoundException;
import com.example.pos_backend.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 设备服务类
 * 提供设备相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeviceService {

    private final DeviceRepository deviceRepository;

    /**
     * 创建新设备
     *
     * @param device 设备实体
     * @return 创建的设备
     */
    public Device createDevice(Device device) {
        log.info("创建新设备: {}", device.getDeviceName());
        
        // 设置创建时间和更新时间
        Instant now = Instant.now();
        device.setCreatedAt(now);
        device.setUpdatedAt(now);
        device.setIsDeleted(GlobalConstants.Database.NOT_DELETED);
        
        // 设置默认值
        if (device.getStatus() == null) {
            device.setStatus("OFFLINE");
        }
        if (device.getRegisteredAt() == null) {
            device.setRegisteredAt(now);
        }
        
        Device savedDevice = deviceRepository.save(device);
        log.info("设备创建成功，ID: {}", savedDevice.getId());
        return savedDevice;
    }

    /**
     * 根据ID获取设备
     *
     * @param id 设备ID
     * @return 设备信息
     * @throws DeviceNotFoundException 如果设备不存在
     */
    @Transactional(readOnly = true)
    public Device getDeviceById(Long id) {
        log.info("根据ID获取设备: {}", id);
        return deviceRepository.findById(id)
                .filter(device -> !GlobalConstants.Database.IS_DELETED.equals(device.getIsDeleted()))
                .orElseThrow(() -> DeviceNotFoundException.withId(id));
    }

    /**
     * 获取所有设备（未删除）
     *
     * @return 设备列表
     */
    @Transactional(readOnly = true)
    public List<Device> getAllDevices() {
        log.info("获取所有设备");
        return deviceRepository.findByIsDeleted(GlobalConstants.Database.NOT_DELETED);
    }

    /**
     * 分页获取所有设备（未删除）
     *
     * @param pageable 分页参数
     * @return 分页设备列表
     */
    @Transactional(readOnly = true)
    public Page<Device> getAllDevices(Pageable pageable) {
        log.info("分页获取设备，页码: {}, 大小: {}", pageable.getPageNumber(), pageable.getPageSize());
        return deviceRepository.findAll(pageable);
    }

    /**
     * 更新设备信息
     *
     * @param id     设备ID
     * @param device 更新的设备信息
     * @return 更新后的设备
     * @throws DeviceNotFoundException 如果设备不存在
     */
    public Device updateDevice(Long id, Device device) {
        log.info("更新设备信息，ID: {}", id);
        
        Device existingDevice = getDeviceById(id);
        
        // 更新字段
        if (device.getDeviceName() != null) {
            existingDevice.setDeviceName(device.getDeviceName());
        }
        if (device.getDeviceType() != null) {
            existingDevice.setDeviceType(device.getDeviceType());
        }
        if (device.getMacAddress() != null) {
            existingDevice.setMacAddress(device.getMacAddress());
        }
        if (device.getIpAddress() != null) {
            existingDevice.setIpAddress(device.getIpAddress());
        }
        if (device.getStatus() != null) {
            existingDevice.setStatus(device.getStatus());
        }
        if (device.getLastOnline() != null) {
            existingDevice.setLastOnline(device.getLastOnline());
        }
        if (device.getUpdatedBy() != null) {
            existingDevice.setUpdatedBy(device.getUpdatedBy());
        }
        
        // 更新时间
        existingDevice.setUpdatedAt(Instant.now());
        
        Device updatedDevice = deviceRepository.save(existingDevice);
        log.info("设备更新成功，ID: {}", updatedDevice.getId());
        return updatedDevice;
    }

    /**
     * 删除设备（软删除）
     *
     * @param id 设备ID
     * @throws DeviceNotFoundException 如果设备不存在
     */
    public void deleteDevice(Long id) {
        log.info("删除设备，ID: {}", id);
        
        Device device = getDeviceById(id);
        device.setIsDeleted(GlobalConstants.Database.IS_DELETED);
        device.setUpdatedAt(Instant.now());
        
        deviceRepository.save(device);
        log.info("设备删除成功，ID: {}", id);
    }

    /**
     * 根据设备名称查找设备
     *
     * @param deviceName 设备名称
     * @return 设备信息
     */
    @Transactional(readOnly = true)
    public Optional<Device> findByDeviceName(String deviceName) {
        log.info("根据设备名称查找设备: {}", deviceName);
        return deviceRepository.findByDeviceName(deviceName)
                .filter(device -> !GlobalConstants.Database.IS_DELETED.equals(device.getIsDeleted()));
    }

    /**
     * 根据设备类型获取设备列表
     *
     * @param deviceType 设备类型
     * @return 设备列表
     */
    @Transactional(readOnly = true)
    public List<Device> findByDeviceType(String deviceType) {
        log.info("根据设备类型获取设备: {}", deviceType);
        return deviceRepository.findByDeviceType(deviceType).stream()
                .filter(device -> !GlobalConstants.Database.IS_DELETED.equals(device.getIsDeleted()))
                .toList();
    }

    /**
     * 根据状态获取设备列表
     *
     * @param status 设备状态
     * @return 设备列表
     */
    @Transactional(readOnly = true)
    public List<Device> findByStatus(String status) {
        log.info("根据状态获取设备: {}", status);
        return deviceRepository.findByStatusAndIsDeleted(status, GlobalConstants.Database.NOT_DELETED);
    }

    /**
     * 根据MAC地址查找设备
     *
     * @param macAddress MAC地址
     * @return 设备信息
     */
    @Transactional(readOnly = true)
    public Optional<Device> findByMacAddress(String macAddress) {
        log.info("根据MAC地址查找设备: {}", macAddress);
        return deviceRepository.findByMacAddress(macAddress)
                .filter(device -> !GlobalConstants.Database.IS_DELETED.equals(device.getIsDeleted()));
    }

    /**
     * 根据IP地址查找设备
     *
     * @param ipAddress IP地址
     * @return 设备信息
     */
    @Transactional(readOnly = true)
    public Optional<Device> findByIpAddress(String ipAddress) {
        log.info("根据IP地址查找设备: {}", ipAddress);
        return deviceRepository.findByIpAddress(ipAddress)
                .filter(device -> !GlobalConstants.Database.IS_DELETED.equals(device.getIsDeleted()));
    }

    /**
     * 根据设备名称模糊查询
     *
     * @param deviceName 设备名称关键字
     * @return 设备列表
     */
    @Transactional(readOnly = true)
    public List<Device> findByDeviceNameContaining(String deviceName) {
        log.info("根据设备名称模糊查询: {}", deviceName);
        return deviceRepository.findByDeviceNameContaining(deviceName).stream()
                .filter(device -> !GlobalConstants.Database.IS_DELETED.equals(device.getIsDeleted()))
                .toList();
    }

    /**
     * 获取在线设备列表
     *
     * @return 在线设备列表
     */
    @Transactional(readOnly = true)
    public List<Device> getOnlineDevices() {
        log.info("获取在线设备列表");
        return deviceRepository.findOnlineDevices();
    }

    /**
     * 获取离线设备列表
     *
     * @return 离线设备列表
     */
    @Transactional(readOnly = true)
    public List<Device> getOfflineDevices() {
        log.info("获取离线设备列表");
        return deviceRepository.findOfflineDevices();
    }

    /**
     * 更新设备状态
     *
     * @param id     设备ID
     * @param status 新状态
     * @return 更新后的设备
     * @throws DeviceNotFoundException 如果设备不存在
     */
    public Device updateDeviceStatus(Long id, String status) {
        log.info("更新设备状态，ID: {}, 状态: {}", id, status);
        
        Device device = getDeviceById(id);
        device.setStatus(status);
        device.setUpdatedAt(Instant.now());
        
        // 如果状态变为在线，更新最后在线时间
        if ("ONLINE".equals(status)) {
            device.setLastOnline(Instant.now());
        }
        
        Device updatedDevice = deviceRepository.save(device);
        log.info("设备状态更新成功，ID: {}", updatedDevice.getId());
        return updatedDevice;
    }

    /**
     * 更新设备最后在线时间
     *
     * @param id 设备ID
     * @throws DeviceNotFoundException 如果设备不存在
     */
    public void updateLastOnlineTime(Long id) {
        log.info("更新设备最后在线时间，ID: {}", id);
        Device device = getDeviceById(id);
        device.setLastOnline(Instant.now());
        device.setUpdatedAt(Instant.now());
        deviceRepository.save(device);
    }

    /**
     * 根据创建者获取设备列表
     *
     * @param createdBy 创建者ID
     * @return 设备列表
     */
    @Transactional(readOnly = true)
    public List<Device> findByCreatedBy(Long createdBy) {
        log.info("根据创建者获取设备: {}", createdBy);
        return deviceRepository.findByCreatedBy(createdBy).stream()
                .filter(device -> !GlobalConstants.Database.IS_DELETED.equals(device.getIsDeleted()))
                .toList();
    }

    /**
     * 统计指定状态的设备数量
     *
     * @param status 设备状态
     * @return 设备数量
     */
    @Transactional(readOnly = true)
    public long countByStatus(String status) {
        log.info("统计设备数量，状态: {}", status);
        return deviceRepository.countByStatusAndNotDeleted(status);
    }

    /**
     * 统计指定设备类型的设备数量
     *
     * @param deviceType 设备类型
     * @return 设备数量
     */
    @Transactional(readOnly = true)
    public long countByDeviceType(String deviceType) {
        log.info("统计设备数量，类型: {}", deviceType);
        return deviceRepository.countByDeviceTypeAndNotDeleted(deviceType);
    }

    /**
     * 查找长时间未在线的设备
     *
     * @param threshold 时间阈值
     * @return 设备列表
     */
    @Transactional(readOnly = true)
    public List<Device> findDevicesNotOnlineSince(Instant threshold) {
        log.info("查找长时间未在线的设备，阈值: {}", threshold);
        return deviceRepository.findDevicesNotOnlineSince(threshold);
    }

    /**
     * 根据关键字搜索设备（设备名称或设备类型）
     *
     * @param keyword 搜索关键字
     * @return 设备列表
     */
    @Transactional(readOnly = true)
    public List<Device> searchDevices(String keyword) {
        log.info("搜索设备，关键字: {}", keyword);
        return deviceRepository.findByDeviceNameOrDeviceTypeContaining(keyword);
    }
}
