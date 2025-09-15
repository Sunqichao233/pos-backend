package com.example.pos_backend.repository;

import com.example.pos_backend.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Merchant实体的Repository接口
 * 提供商家相关的数据访问方法
 */
@Repository
public interface MerchantRepository extends JpaRepository<Merchant, String>, JpaSpecificationExecutor<Merchant> {

    /**
     * 根据邮箱查找商家
     */
    Optional<Merchant> findByEmail(String email);

    /**
     * 根据邮箱查找商家（排除已删除）
     */
    Optional<Merchant> findByEmailAndIsDeleted(String email, Boolean isDeleted);

    /**
     * 根据企业名称查找商家
     */
    List<Merchant> findByBusinessName(String businessName);

    /**
     * 根据企业名称模糊查询
     */
    List<Merchant> findByBusinessNameContaining(String businessName);

    /**
     * 根据行业查找商家
     */
    List<Merchant> findByIndustry(String industry);

    /**
     * 根据状态查找商家
     */
    List<Merchant> findByStatus(String status);

    /**
     * 根据国家查找商家
     */
    List<Merchant> findByCountry(String country);

    /**
     * 根据币种查找商家
     */
    List<Merchant> findByCurrency(String currency);

    /**
     * 根据状态和删除标记查找商家
     */
    List<Merchant> findByStatusAndIsDeleted(String status, Boolean isDeleted);

    /**
     * 查找未删除的商家
     */
    List<Merchant> findByIsDeleted(Boolean isDeleted);

    /**
     * 根据创建时间范围查找商家
     */
    List<Merchant> findByCreatedAtBetween(Instant startTime, Instant endTime);

    /**
     * 根据创建者查找商家
     */
    List<Merchant> findByCreatedBy(String createdBy);

    /**
     * 根据更新者查找商家
     */
    List<Merchant> findByUpdatedBy(String updatedBy);

    /**
     * 查找活跃商家（状态为ACTIVE且未删除）
     */
    @Query("SELECT m FROM Merchant m WHERE m.status = 'ACTIVE' AND m.isDeleted = false")
    List<Merchant> findActiveMerchants();

    /**
     * 查找非活跃商家（状态不为ACTIVE或已删除）
     */
    @Query("SELECT m FROM Merchant m WHERE m.status != 'ACTIVE' OR m.isDeleted = true")
    List<Merchant> findInactiveMerchants();

    /**
     * 根据行业和时间范围查找商家
     */
    @Query("SELECT m FROM Merchant m WHERE m.industry = :industry AND m.createdAt BETWEEN :startTime AND :endTime AND m.isDeleted = false")
    List<Merchant> findByIndustryAndCreatedAtBetween(@Param("industry") String industry, 
                                                    @Param("startTime") Instant startTime, 
                                                    @Param("endTime") Instant endTime);

    /**
     * 统计指定状态的商家数量
     */
    @Query("SELECT COUNT(m) FROM Merchant m WHERE m.status = :status AND m.isDeleted = false")
    long countByStatusAndNotDeleted(@Param("status") String status);

    /**
     * 统计指定行业的商家数量
     */
    @Query("SELECT COUNT(m) FROM Merchant m WHERE m.industry = :industry AND m.isDeleted = false")
    long countByIndustryAndNotDeleted(@Param("industry") String industry);

    /**
     * 根据企业名称或邮箱模糊查询
     */
    @Query("SELECT m FROM Merchant m WHERE (m.businessName LIKE %:keyword% OR m.email LIKE %:keyword%) AND m.isDeleted = false")
    List<Merchant> findByBusinessNameOrEmailContaining(@Param("keyword") String keyword);

    /**
     * 查找指定创建者在指定时间范围内创建的商家
     */
    @Query("SELECT m FROM Merchant m WHERE m.createdBy = :createdBy AND m.createdAt BETWEEN :startTime AND :endTime AND m.isDeleted = false")
    List<Merchant> findByCreatedByAndCreatedAtBetween(@Param("createdBy") String createdBy, 
                                                     @Param("startTime") Instant startTime, 
                                                     @Param("endTime") Instant endTime);

    /**
     * 根据国家和币种查找商家
     */
    @Query("SELECT m FROM Merchant m WHERE m.country = :country AND m.currency = :currency AND m.isDeleted = false")
    List<Merchant> findByCountryAndCurrency(@Param("country") String country, @Param("currency") String currency);

    /**
     * 检查邮箱是否已存在（排除指定商家ID）
     */
    @Query("SELECT COUNT(m) > 0 FROM Merchant m WHERE m.email = :email AND m.id != :merchantId AND m.isDeleted = false")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("merchantId") String merchantId);

    /**
     * 检查邮箱是否已存在
     */
    boolean existsByEmailAndIsDeleted(String email, Boolean isDeleted);

    /**
     * 根据多个状态查找商家
     */
    @Query("SELECT m FROM Merchant m WHERE m.status IN :statuses AND m.isDeleted = false")
    List<Merchant> findByStatusIn(@Param("statuses") List<String> statuses);

    /**
     * 查找最近注册的商家
     */
    @Query("SELECT m FROM Merchant m WHERE m.isDeleted = false ORDER BY m.createdAt DESC")
    List<Merchant> findRecentMerchants();

    /**
     * 根据行业分组统计商家数量
     */
    @Query("SELECT m.industry, COUNT(m) FROM Merchant m WHERE m.isDeleted = false GROUP BY m.industry")
    List<Object[]> countMerchantsByIndustry();

    /**
     * 根据国家分组统计商家数量
     */
    @Query("SELECT m.country, COUNT(m) FROM Merchant m WHERE m.isDeleted = false GROUP BY m.country")
    List<Object[]> countMerchantsByCountry();
}
