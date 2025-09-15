package com.example.pos_backend.repository;

import com.example.pos_backend.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Device实体的Repository接口
 * 提供设备相关的数据访问方法
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, String>, JpaSpecificationExecutor<Device> {

    /**
     * 根据设备名称查找设备
     */
    Optional<Device> findByDeviceName(String deviceName);

    /**
     * 根据设备类型查找设备列表
     */
    List<Device> findByDeviceType(String deviceType);

    /**
     * 根据MAC地址查找设备
     */
    Optional<Device> findByMacAddress(String macAddress);

    /**
     * 根据IP地址查找设备
     */
    Optional<Device> findByIpAddress(String ipAddress);

    /**
     * 根据状态查找设备列表
     */
    List<Device> findByStatus(String status);

    /**
     * 根据设备名称模糊查询
     */
    List<Device> findByDeviceNameContaining(String deviceName);

    /**
     * 根据设备类型和状态查找设备
     */
    List<Device> findByDeviceTypeAndStatus(String deviceType, String status);

    /**
     * 根据状态和删除标记查找设备
     */
    List<Device> findByStatusAndIsDeleted(String status, Boolean isDeleted);

    /**
     * 查找未删除的设备
     */
    List<Device> findByIsDeleted(Boolean isDeleted);

    /**
     * 根据最后在线时间范围查找设备
     */
    List<Device> findByLastOnlineBetween(Instant startTime, Instant endTime);

    /**
     * 根据注册时间范围查找设备
     */
    List<Device> findByRegisteredAtBetween(Instant startTime, Instant endTime);

    /**
     * 根据创建时间范围查找设备
     */
    List<Device> findByCreatedAtBetween(Instant startTime, Instant endTime);

    /**
     * 根据创建者查找设备
     */
    List<Device> findByCreatedBy(String createdBy);

    /**
     * 根据更新者查找设备
     */
    List<Device> findByUpdatedBy(String updatedBy);

    /**
     * 查找在线设备（状态为ONLINE且未删除）
     */
    @Query("SELECT d FROM Device d WHERE d.status = 'ONLINE' AND d.isDeleted = false")
    List<Device> findOnlineDevices();

    /**
     * 查找离线设备（状态为OFFLINE且未删除）
     */
    @Query("SELECT d FROM Device d WHERE d.status = 'OFFLINE' AND d.isDeleted = false")
    List<Device> findOfflineDevices();

    /**
     * 根据设备类型和时间范围查找设备
     */
    @Query("SELECT d FROM Device d WHERE d.deviceType = :deviceType AND d.createdAt BETWEEN :startTime AND :endTime AND d.isDeleted = false")
    List<Device> findByDeviceTypeAndCreatedAtBetween(@Param("deviceType") String deviceType, 
                                                    @Param("startTime") Instant startTime, 
                                                    @Param("endTime") Instant endTime);

    /**
     * 统计指定状态的设备数量
     */
    @Query("SELECT COUNT(d) FROM Device d WHERE d.status = :status AND d.isDeleted = false")
    long countByStatusAndNotDeleted(@Param("status") String status);

    /**
     * 统计指定设备类型的设备数量
     */
    @Query("SELECT COUNT(d) FROM Device d WHERE d.deviceType = :deviceType AND d.isDeleted = false")
    long countByDeviceTypeAndNotDeleted(@Param("deviceType") String deviceType);

    /**
     * 查找长时间未在线的设备
     */
    @Query("SELECT d FROM Device d WHERE d.lastOnline < :threshold AND d.isDeleted = false")
    List<Device> findDevicesNotOnlineSince(@Param("threshold") Instant threshold);

    /**
     * 根据设备名称或设备类型模糊查询
     */
    @Query("SELECT d FROM Device d WHERE (d.deviceName LIKE %:keyword% OR d.deviceType LIKE %:keyword%) AND d.isDeleted = false")
    List<Device> findByDeviceNameOrDeviceTypeContaining(@Param("keyword") String keyword);
}
