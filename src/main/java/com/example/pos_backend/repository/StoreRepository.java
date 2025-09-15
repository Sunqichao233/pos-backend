package com.example.pos_backend.repository;

import com.example.pos_backend.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Store实体的Repository接口
 * 提供门店相关的数据访问方法
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, String>, JpaSpecificationExecutor<Store> {

    /**
     * 根据商家ID查找门店
     */
    List<Store> findByMerchantId(String merchantId);

    /**
     * 根据商家ID查找门店（排除已删除）
     */
    List<Store> findByMerchantIdAndIsDeleted(String merchantId, Boolean isDeleted);

    /**
     * 根据门店名称查找门店
     */
    List<Store> findByStoreName(String storeName);

    /**
     * 根据门店名称模糊查询
     */
    List<Store> findByStoreNameContaining(String storeName);

    /**
     * 根据状态查找门店
     */
    List<Store> findByStatus(String status);

    /**
     * 根据时区查找门店
     */
    List<Store> findByTimezone(String timezone);

    /**
     * 根据币种查找门店
     */
    List<Store> findByCurrency(String currency);

    /**
     * 查找未删除的门店
     */
    List<Store> findByIsDeleted(Boolean isDeleted);

    /**
     * 查找活跃门店（状态为ACTIVE且未删除）
     */
    @Query("SELECT s FROM Store s WHERE s.status = 'ACTIVE' AND s.isDeleted = false")
    List<Store> findActiveStores();

    /**
     * 根据商家ID查找活跃门店
     */
    @Query("SELECT s FROM Store s WHERE s.merchantId = :merchantId AND s.status = 'ACTIVE' AND s.isDeleted = false")
    List<Store> findActiveStoresByMerchantId(@Param("merchantId") String merchantId);

    /**
     * 根据商家ID查找默认门店（第一个创建的门店）
     */
    @Query("SELECT s FROM Store s WHERE s.merchantId = :merchantId AND s.isDeleted = false ORDER BY s.createdAt ASC")
    Optional<Store> findDefaultStoreByMerchantId(@Param("merchantId") String merchantId);

    /**
     * 统计商家的门店数量
     */
    @Query("SELECT COUNT(s) FROM Store s WHERE s.merchantId = :merchantId AND s.isDeleted = false")
    long countByMerchantIdAndNotDeleted(@Param("merchantId") String merchantId);

    /**
     * 统计指定状态的门店数量
     */
    @Query("SELECT COUNT(s) FROM Store s WHERE s.status = :status AND s.isDeleted = false")
    long countByStatusAndNotDeleted(@Param("status") String status);

    /**
     * 根据地址模糊查询门店
     */
    List<Store> findByAddressContaining(String address);

    /**
     * 检查门店名称是否已存在（同一商家下）
     */
    @Query("SELECT COUNT(s) > 0 FROM Store s WHERE s.merchantId = :merchantId AND s.storeName = :storeName AND s.isDeleted = false")
    boolean existsByMerchantIdAndStoreNameAndIsDeleted(@Param("merchantId") String merchantId, 
                                                      @Param("storeName") String storeName, 
                                                      @Param("isDeleted") Boolean isDeleted);

    /**
     * 检查门店名称是否已存在（排除指定门店ID）
     */
    @Query("SELECT COUNT(s) > 0 FROM Store s WHERE s.merchantId = :merchantId AND s.storeName = :storeName AND s.id != :storeId AND s.isDeleted = false")
    boolean existsByMerchantIdAndStoreNameAndIdNot(@Param("merchantId") String merchantId, 
                                                  @Param("storeName") String storeName, 
                                                  @Param("storeId") String storeId);
}
