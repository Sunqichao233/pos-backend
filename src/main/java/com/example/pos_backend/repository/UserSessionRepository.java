package com.example.pos_backend.repository;

import com.example.pos_backend.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * UserSession实体的Repository接口
 * 提供用户会话相关的数据访问方法
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String>, JpaSpecificationExecutor<UserSession> {

    /**
     * 根据用户ID查找活跃会话
     */
    List<UserSession> findByUserIdAndStatus(String userId, String status);

    /**
     * 根据用户ID查找所有会话（排除已删除）
     */
    List<UserSession> findByUserIdAndIsDeleted(String userId, Boolean isDeleted);

    /**
     * 根据Access Token查找会话
     */
    Optional<UserSession> findByAccessToken(String accessToken);

    /**
     * 根据Refresh Token查找会话
     */
    Optional<UserSession> findByRefreshToken(String refreshToken);

    /**
     * 根据设备ID查找会话
     */
    List<UserSession> findByDeviceId(String deviceId);

    /**
     * 根据IP地址查找会话
     */
    List<UserSession> findByIpAddress(String ipAddress);

    /**
     * 根据状态查找会话
     */
    List<UserSession> findByStatus(String status);

    /**
     * 查找活跃会话
     */
    @Query("SELECT s FROM UserSession s WHERE s.status = 'ACTIVE' AND s.isDeleted = false")
    List<UserSession> findActiveSessions();

    /**
     * 查找过期的Access Token会话
     */
    @Query("SELECT s FROM UserSession s WHERE s.accessTokenExpiresAt < :currentTime AND s.status = 'ACTIVE' AND s.isDeleted = false")
    List<UserSession> findExpiredAccessTokenSessions(@Param("currentTime") Instant currentTime);

    /**
     * 查找过期的Refresh Token会话
     */
    @Query("SELECT s FROM UserSession s WHERE s.refreshTokenExpiresAt < :currentTime AND s.status = 'ACTIVE' AND s.isDeleted = false")
    List<UserSession> findExpiredRefreshTokenSessions(@Param("currentTime") Instant currentTime);

    /**
     * 根据用户ID查找最新的活跃会话
     */
    @Query("SELECT s FROM UserSession s WHERE s.userId = :userId AND s.status = 'ACTIVE' AND s.isDeleted = false ORDER BY s.lastActivityAt DESC")
    List<UserSession> findLatestActiveSessionsByUserId(@Param("userId") String userId);

    /**
     * 统计用户的活跃会话数量
     */
    @Query("SELECT COUNT(s) FROM UserSession s WHERE s.userId = :userId AND s.status = 'ACTIVE' AND s.isDeleted = false")
    long countActiveSessionsByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID和设备ID查找会话
     */
    Optional<UserSession> findByUserIdAndDeviceIdAndStatus(String userId, String deviceId, String status);

    /**
     * 查找长时间未活动的会话
     */
    @Query("SELECT s FROM UserSession s WHERE s.lastActivityAt < :threshold AND s.status = 'ACTIVE' AND s.isDeleted = false")
    List<UserSession> findInactiveSessionsSince(@Param("threshold") Instant threshold);

    /**
     * 批量更新过期会话状态
     */
    @Query("UPDATE UserSession s SET s.status = 'EXPIRED', s.updatedAt = :currentTime WHERE s.accessTokenExpiresAt < :currentTime AND s.status = 'ACTIVE'")
    int batchExpireAccessTokenSessions(@Param("currentTime") Instant currentTime);

    /**
     * 根据用户ID删除所有会话（软删除）
     */
    @Query("UPDATE UserSession s SET s.isDeleted = true, s.status = 'DELETED', s.updatedAt = :currentTime WHERE s.userId = :userId AND s.isDeleted = false")
    int deleteAllSessionsByUserId(@Param("userId") String userId, @Param("currentTime") Instant currentTime);
}
