package com.example.pos_backend.repository;

import com.example.pos_backend.entity.DeviceCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * DeviceCode实体的Repository接口
 * 提供设备码相关的数据访问方法
 */
@Repository
public interface DeviceCodeRepository extends JpaRepository<DeviceCode, Long>, JpaSpecificationExecutor<DeviceCode> {

    /**
     * 根据设备码查找设备码记录
     */
    Optional<DeviceCode> findByDeviceCode(String deviceCode);

    /**
     * 根据设备ID查找设备码列表
     */
    List<DeviceCode> findByDeviceId(Long deviceId);

    /**
     * 根据状态查找设备码列表
     */
    List<DeviceCode> findByStatus(String status);

    /**
     * 根据设备码和状态查找设备码记录
     */
    Optional<DeviceCode> findByDeviceCodeAndStatus(String deviceCode, String status);

    /**
     * 根据设备ID和状态查找设备码列表
     */
    List<DeviceCode> findByDeviceIdAndStatus(Long deviceId, String status);

    /**
     * 根据状态和删除标记查找设备码列表
     */
    List<DeviceCode> findByStatusAndIsDeleted(String status, Boolean isDeleted);

    /**
     * 查找未删除的设备码
     */
    List<DeviceCode> findByIsDeleted(Boolean isDeleted);

    /**
     * 根据发行时间范围查找设备码
     */
    List<DeviceCode> findByIssuedAtBetween(Instant startTime, Instant endTime);

    /**
     * 根据过期时间范围查找设备码
     */
    List<DeviceCode> findByExpiredAtBetween(Instant startTime, Instant endTime);

    /**
     * 根据绑定时间范围查找设备码
     */
    List<DeviceCode> findByBoundAtBetween(Instant startTime, Instant endTime);

    /**
     * 根据创建时间范围查找设备码
     */
    List<DeviceCode> findByCreatedAtBetween(Instant startTime, Instant endTime);

    /**
     * 根据创建者查找设备码
     */
    List<DeviceCode> findByCreatedBy(Long createdBy);

    /**
     * 根据更新者查找设备码
     */
    List<DeviceCode> findByUpdatedBy(Long updatedBy);

    /**
     * 根据设备码模糊查询
     */
    List<DeviceCode> findByDeviceCodeContaining(String deviceCode);

    /**
     * 查找未使用的设备码（状态为UNUSED且未删除）
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.status = 'UNUSED' AND dc.isDeleted = false")
    List<DeviceCode> findUnusedDeviceCodes();

    /**
     * 查找已绑定的设备码（状态为BOUND且未删除）
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.status = 'BOUND' AND dc.isDeleted = false")
    List<DeviceCode> findBoundDeviceCodes();

    /**
     * 查找已过期的设备码（状态为EXPIRED或过期时间小于当前时间且未删除）
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE (dc.status = 'EXPIRED' OR dc.expiredAt < :currentTime) AND dc.isDeleted = false")
    List<DeviceCode> findExpiredDeviceCodes(@Param("currentTime") Instant currentTime);

    /**
     * 根据设备ID和状态查找有效的设备码
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.deviceId = :deviceId AND dc.status = :status AND dc.isDeleted = false")
    List<DeviceCode> findValidDeviceCodesByDeviceIdAndStatus(@Param("deviceId") Long deviceId, @Param("status") String status);

    /**
     * 统计指定状态的设备码数量
     */
    @Query("SELECT COUNT(dc) FROM DeviceCode dc WHERE dc.status = :status AND dc.isDeleted = false")
    long countByStatusAndNotDeleted(@Param("status") String status);

    /**
     * 统计指定设备的设备码数量
     */
    @Query("SELECT COUNT(dc) FROM DeviceCode dc WHERE dc.deviceId = :deviceId AND dc.isDeleted = false")
    long countByDeviceIdAndNotDeleted(@Param("deviceId") Long deviceId);

    /**
     * 查找即将过期的设备码（过期时间在指定时间之前且状态为UNUSED）
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.expiredAt <= :threshold AND dc.status = 'UNUSED' AND dc.isDeleted = false")
    List<DeviceCode> findDeviceCodesExpiringBefore(@Param("threshold") Instant threshold);

    /**
     * 根据发行时间和状态查找设备码
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.issuedAt BETWEEN :startTime AND :endTime AND dc.status = :status AND dc.isDeleted = false")
    List<DeviceCode> findByIssuedAtBetweenAndStatus(@Param("startTime") Instant startTime, 
                                                   @Param("endTime") Instant endTime, 
                                                   @Param("status") String status);

    /**
     * 查找指定创建者在指定时间范围内创建的设备码
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.createdBy = :createdBy AND dc.createdAt BETWEEN :startTime AND :endTime AND dc.isDeleted = false")
    List<DeviceCode> findByCreatedByAndCreatedAtBetween(@Param("createdBy") Long createdBy, 
                                                       @Param("startTime") Instant startTime, 
                                                       @Param("endTime") Instant endTime);

    /**
     * 根据设备码前缀查找设备码（支持批量查询）
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.deviceCode LIKE :prefix% AND dc.isDeleted = false")
    List<DeviceCode> findByDeviceCodeStartingWith(@Param("prefix") String prefix);
}
