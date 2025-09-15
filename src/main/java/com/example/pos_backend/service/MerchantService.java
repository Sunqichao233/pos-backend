package com.example.pos_backend.service;

import com.example.pos_backend.dto.*;
import com.example.pos_backend.entity.Merchant;
import com.example.pos_backend.entity.Store;
import com.example.pos_backend.repository.MerchantRepository;
import com.example.pos_backend.repository.StoreRepository;
import com.example.pos_backend.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 商家服务类
 * 处理商家注册、认证、管理等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Random RANDOM = new Random();

    /**
     * 商家注册
     */
    @Transactional
    public MerchantResponseDTO registerMerchant(MerchantRequestDTO requestDTO) {
        // 1. 验证邮箱是否已存在
        if (merchantRepository.existsByEmailAndIsDeleted(requestDTO.getEmail(), false)) {
            throw new BusinessException("邮箱已存在: " + requestDTO.getEmail());
        }

        // 2. 创建商家实体
        Merchant merchant = MerchantMapper.toEntity(requestDTO);
        merchant.setId(generateMerchantId());
        merchant.setPasswordHash(passwordEncoder.encode(requestDTO.getPassword()));
        merchant.setCreatedAt(Instant.now());
        merchant.setCreatedBy(merchant.getId()); // 自己创建自己

        // 3. 保存商家
        Merchant savedMerchant = merchantRepository.save(merchant);
        log.info("Created merchant: {} with ID: {}", savedMerchant.getBusinessName(), savedMerchant.getId());

        // 4. 创建默认门店
        Store defaultStore = MerchantMapper.toStoreEntity(requestDTO.getStore(), savedMerchant.getId());
        defaultStore.setId(generateLocationId());
        defaultStore.setCreatedAt(Instant.now());
        defaultStore.setCreatedBy(savedMerchant.getId());

        Store savedStore = storeRepository.save(defaultStore);
        log.info("Created default store: {} with ID: {}", savedStore.getStoreName(), savedStore.getId());

        // 5. 返回响应
        return MerchantMapper.toResponseDTO(savedMerchant, savedStore);
    }

    /**
     * 根据ID获取商家
     */
    public MerchantResponseDTO getMerchantById(String merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在: " + merchantId));
        
        if (merchant.getIsDeleted()) {
            throw new BusinessException("商家已被删除: " + merchantId);
        }

        return MerchantMapper.toResponseDTO(merchant);
    }

    /**
     * 根据邮箱获取商家
     */
    public MerchantResponseDTO getMerchantByEmail(String email) {
        Merchant merchant = merchantRepository.findByEmailAndIsDeleted(email, false)
                .orElseThrow(() -> new BusinessException("商家不存在: " + email));

        return MerchantMapper.toResponseDTO(merchant);
    }

    /**
     * 获取所有活跃商家
     */
    public List<MerchantResponseDTO> getAllActiveMerchants() {
        return merchantRepository.findActiveMerchants().stream()
                .map(MerchantMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据行业获取商家
     */
    public List<MerchantResponseDTO> getMerchantsByIndustry(String industry) {
        return merchantRepository.findByIndustry(industry).stream()
                .filter(merchant -> !merchant.getIsDeleted())
                .map(MerchantMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 更新商家信息
     */
    @Transactional
    public MerchantResponseDTO updateMerchant(String merchantId, MerchantUpdateDTO updateDTO) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在: " + merchantId));

        if (merchant.getIsDeleted()) {
            throw new BusinessException("商家已被删除: " + merchantId);
        }

        // 检查邮箱是否被其他商家使用
        if (updateDTO.getEmail() != null && 
            !updateDTO.getEmail().equals(merchant.getEmail()) &&
            merchantRepository.existsByEmailAndIdNot(updateDTO.getEmail(), merchantId)) {
            throw new BusinessException("邮箱已被其他商家使用: " + updateDTO.getEmail());
        }

        MerchantMapper.updateEntityFromDTO(updateDTO, merchant);
        merchant.setUpdatedAt(Instant.now());

        Merchant updatedMerchant = merchantRepository.save(merchant);
        log.info("Updated merchant: {} with ID: {}", updatedMerchant.getBusinessName(), updatedMerchant.getId());

        return MerchantMapper.toResponseDTO(updatedMerchant);
    }

    /**
     * 删除商家（软删除）
     */
    @Transactional
    public void deleteMerchant(String merchantId, String deletedBy) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在: " + merchantId));

        if (merchant.getIsDeleted()) {
            throw new BusinessException("商家已被删除: " + merchantId);
        }

        merchant.setIsDeleted(true);
        merchant.setStatus("INACTIVE");
        merchant.setUpdatedAt(Instant.now());
        merchant.setUpdatedBy(deletedBy);

        merchantRepository.save(merchant);
        log.info("Deleted merchant: {} with ID: {}", merchant.getBusinessName(), merchant.getId());

        // 同时软删除该商家下的所有门店
        List<Store> stores = storeRepository.findByMerchantIdAndIsDeleted(merchantId, false);
        stores.forEach(store -> {
            store.setIsDeleted(true);
            store.setStatus("INACTIVE");
            store.setUpdatedAt(Instant.now());
            store.setUpdatedBy(deletedBy);
        });
        storeRepository.saveAll(stores);
        log.info("Deleted {} stores for merchant: {}", stores.size(), merchantId);
    }

    /**
     * 商家认证（登录）
     */
    public MerchantResponseDTO authenticateMerchant(String email, String password) {
        Merchant merchant = merchantRepository.findByEmailAndIsDeleted(email, false)
                .orElseThrow(() -> new BusinessException("邮箱或密码错误"));

        if (!passwordEncoder.matches(password, merchant.getPasswordHash())) {
            throw new BusinessException("邮箱或密码错误");
        }

        if (!"ACTIVE".equals(merchant.getStatus())) {
            throw new BusinessException("商家账户已被禁用");
        }

        log.info("Merchant authenticated: {} with ID: {}", merchant.getBusinessName(), merchant.getId());
        return MerchantMapper.toResponseDTO(merchant);
    }

    /**
     * 搜索商家
     */
    public List<MerchantResponseDTO> searchMerchants(String keyword) {
        return merchantRepository.findByBusinessNameOrEmailContaining(keyword).stream()
                .map(MerchantMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 统计商家数量
     */
    public long countActiveMerchants() {
        return merchantRepository.countByStatusAndNotDeleted("ACTIVE");
    }

    /**
     * 统计各行业商家数量
     */
    public List<Object[]> countMerchantsByIndustry() {
        return merchantRepository.countMerchantsByIndustry();
    }

    /**
     * 生成商家ID - Square风格
     */
    private String generateMerchantId() {
        long timestamp = System.currentTimeMillis();
        int randomNum = RANDOM.nextInt(1000);
        return String.format("MRC-%d-%03d", timestamp, randomNum);
    }

    /**
     * 生成门店ID - Square风格
     */
    private String generateLocationId() {
        long timestamp = System.currentTimeMillis();
        int randomNum = RANDOM.nextInt(1000);
        return String.format("LOC-%d-%03d", timestamp, randomNum);
    }
}
