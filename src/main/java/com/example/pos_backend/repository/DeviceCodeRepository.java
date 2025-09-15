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
public interface DeviceCodeRepository extends JpaRepository<DeviceCode, String>, JpaSpecificationExecutor<DeviceCode> {

    /**
     * 根据设备码查找设备码记录
     */
    Optional<DeviceCode> findByDeviceCode(String deviceCode);

    /**
     * 根据设备ID查找设备码列表
     */
    List<DeviceCode> findByDeviceId(String deviceId);

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
    List<DeviceCode> findByDeviceIdAndStatus(String deviceId, String status);

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
    List<DeviceCode> findByCreatedBy(String createdBy);

    /**
     * 根据更新者查找设备码
     */
    List<DeviceCode> findByUpdatedBy(String updatedBy);

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
    List<DeviceCode> findValidDeviceCodesByDeviceIdAndStatus(@Param("deviceId") String deviceId, @Param("status") String status);

    /**
     * 统计指定状态的设备码数量
     */
    @Query("SELECT COUNT(dc) FROM DeviceCode dc WHERE dc.status = :status AND dc.isDeleted = false")
    long countByStatusAndNotDeleted(@Param("status") String status);

    /**
     * 统计指定设备的设备码数量
     */
    @Query("SELECT COUNT(dc) FROM DeviceCode dc WHERE dc.deviceId = :deviceId AND dc.isDeleted = false")
    long countByDeviceIdAndNotDeleted(@Param("deviceId") String deviceId);

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
    List<DeviceCode> findByCreatedByAndCreatedAtBetween(@Param("createdBy") String createdBy, 
                                                       @Param("startTime") Instant startTime, 
                                                       @Param("endTime") Instant endTime);

    /**
     * 根据设备码前缀查找设备码（支持批量查询）
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.deviceCode LIKE :prefix% AND dc.isDeleted = false")
    List<DeviceCode> findByDeviceCodeStartingWith(@Param("prefix") String prefix);

    /**
     * 根据设备指纹查找设备码
     */
    Optional<DeviceCode> findByDeviceFingerprint(String deviceFingerprint);

    /**
     * 根据设备指纹和状态查找设备码
     */
    List<DeviceCode> findByDeviceFingerprintAndStatus(String deviceFingerprint, String status);

    /**
     * 查找激活尝试次数超过限制的设备码
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.activationAttempts >= dc.maxAttempts AND dc.status = 'UNUSED' AND dc.isDeleted = false")
    List<DeviceCode> findExceededAttemptsDeviceCodes();

    /**
     * 根据激活尝试次数范围查找设备码
     */
    List<DeviceCode> findByActivationAttemptsBetween(Integer minAttempts, Integer maxAttempts);

    /**
     * 查找指定设备的有效激活码（一个设备只能有一个有效码）
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.deviceId = :deviceId AND dc.status = 'UNUSED' AND dc.isDeleted = false ORDER BY dc.createdAt DESC")
    Optional<DeviceCode> findActiveDeviceCodeByDeviceId(@Param("deviceId") String deviceId);

    /**
     * 根据设备指纹查找已绑定的设备码
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.deviceFingerprint = :fingerprint AND dc.status = 'BOUND' AND dc.isDeleted = false")
    Optional<DeviceCode> findBoundDeviceCodeByFingerprint(@Param("fingerprint") String fingerprint);

    /**
     * 统计指定设备指纹的激活尝试总次数
     */
    @Query("SELECT COALESCE(SUM(dc.activationAttempts), 0) FROM DeviceCode dc WHERE dc.deviceFingerprint = :fingerprint AND dc.isDeleted = false")
    int countTotalAttemptsByFingerprint(@Param("fingerprint") String fingerprint);

    /**
     * 查找需要清理的过期未使用设备码（Square风格：过期后自动清理）
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.expiredAt < :currentTime AND dc.status = 'UNUSED' AND dc.isDeleted = false")
    List<DeviceCode> findExpiredUnusedDeviceCodes(@Param("currentTime") Instant currentTime);

    /**
     * 批量更新过期设备码状态
     */
    @Query("UPDATE DeviceCode dc SET dc.status = 'EXPIRED', dc.updatedAt = :currentTime WHERE dc.expiredAt < :currentTime AND dc.status = 'UNUSED' AND dc.isDeleted = false")
    int batchExpireDeviceCodes(@Param("currentTime") Instant currentTime);

    /**
     * 查找可以重新激活的设备码（激活失败但未超过最大尝试次数）
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.activationAttempts < dc.maxAttempts AND dc.status = 'UNUSED' AND dc.expiredAt > :currentTime AND dc.isDeleted = false")
    List<DeviceCode> findRetryableDeviceCodes(@Param("currentTime") Instant currentTime);

    /**
     * 根据设备ID查找最新的设备码（Square风格：一设备一码）
     */
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.deviceId = :deviceId AND dc.isDeleted = false ORDER BY dc.createdAt DESC")
    List<DeviceCode> findLatestDeviceCodesByDeviceId(@Param("deviceId") String deviceId);
}
