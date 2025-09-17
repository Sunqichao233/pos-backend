package com.example.pos_backend.repository;

import com.example.pos_backend.entity.MerchantSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantSessionRepository extends JpaRepository<MerchantSession, String> {

    /**
     * 根据商家ID查找活跃会话
     */
    List<MerchantSession> findByMerchantIdAndStatusAndIsDeleted(String merchantId, String status, Boolean isDeleted);

    /**
     * 根据访问令牌查找会话
     */
    Optional<MerchantSession> findByAccessTokenAndIsDeleted(String accessToken, Boolean isDeleted);

    /**
     * 根据刷新令牌查找会话
     */
    Optional<MerchantSession> findByRefreshTokenAndIsDeleted(String refreshToken, Boolean isDeleted);

    /**
     * 根据商家ID和设备ID查找会话
     */
    Optional<MerchantSession> findByMerchantIdAndDeviceIdAndStatusAndIsDeleted(
            String merchantId, String deviceId, String status, Boolean isDeleted);

    /**
     * 删除过期的会话
     */
    @Query("DELETE FROM MerchantSession ms WHERE " +
           "(ms.accessTokenExpiresAt < :now) OR " +
           "(ms.refreshTokenExpiresAt < :expiredTime) OR " +
           "(ms.status = 'INACTIVE' AND ms.updatedAt < :inactiveTime)")
    void deleteExpiredSessions(@Param("now") Instant now, 
                              @Param("expiredTime") Instant expiredTime, 
                              @Param("inactiveTime") Instant inactiveTime);

    /**
     * 根据商家ID查找所有会话（包括已删除的）
     */
    List<MerchantSession> findByMerchantId(String merchantId);

    /**
     * 统计商家的活跃会话数量
     */
    long countByMerchantIdAndStatusAndIsDeleted(String merchantId, String status, Boolean isDeleted);

    /**
     * 根据IP地址查找会话（安全审计用）
     */
    List<MerchantSession> findByIpAddressAndIsDeleted(String ipAddress, Boolean isDeleted);

    /**
     * 查找即将过期的会话
     */
    @Query("SELECT ms FROM MerchantSession ms WHERE " +
           "ms.accessTokenExpiresAt BETWEEN :now AND :soonExpire AND " +
           "ms.status = 'ACTIVE' AND ms.isDeleted = false")
    List<MerchantSession> findSessionsExpiringSoon(@Param("now") Instant now, 
                                                  @Param("soonExpire") Instant soonExpire);
}
