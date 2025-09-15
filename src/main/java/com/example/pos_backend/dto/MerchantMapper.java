package com.example.pos_backend.dto;

import com.example.pos_backend.entity.Merchant;
import com.example.pos_backend.entity.Store;

/**
 * Merchant实体与DTO转换映射器
 */
public class MerchantMapper {

    /**
     * 请求DTO转实体
     */
    public static Merchant toEntity(MerchantRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        return Merchant.builder()
                .email(requestDTO.getEmail())
                .businessName(requestDTO.getBusinessName())
                .industry(requestDTO.getIndustry())
                .currency(requestDTO.getCurrency())
                .country(requestDTO.getCountry())
                .status("ACTIVE")
                .isDeleted(false)
                .build();
    }

    /**
     * 实体转响应DTO
     */
    public static MerchantResponseDTO toResponseDTO(Merchant merchant) {
        if (merchant == null) {
            return null;
        }

        return MerchantResponseDTO.builder()
                .merchantId(merchant.getId())
                .email(merchant.getEmail())
                .businessName(merchant.getBusinessName())
                .industry(merchant.getIndustry())
                .currency(merchant.getCurrency())
                .country(merchant.getCountry())
                .status(merchant.getStatus())
                .createdAt(merchant.getCreatedAt())
                .updatedAt(merchant.getUpdatedAt())
                .build();
    }

    /**
     * 实体转响应DTO（包含门店信息）
     */
    public static MerchantResponseDTO toResponseDTO(Merchant merchant, Store store) {
        if (merchant == null) {
            return null;
        }

        return MerchantResponseDTO.builder()
                .merchantId(merchant.getId())
                .locationId(store != null ? store.getId() : null)
                .email(merchant.getEmail())
                .businessName(merchant.getBusinessName())
                .industry(merchant.getIndustry())
                .currency(merchant.getCurrency())
                .country(merchant.getCountry())
                .status(merchant.getStatus())
                .createdAt(merchant.getCreatedAt())
                .updatedAt(merchant.getUpdatedAt())
                .message("Merchant and default store created successfully")
                .build();
    }

    /**
     * 更新DTO应用到实体
     */
    public static void updateEntityFromDTO(MerchantUpdateDTO updateDTO, Merchant merchant) {
        if (updateDTO == null || merchant == null) {
            return;
        }

        if (updateDTO.getEmail() != null) {
            merchant.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getBusinessName() != null) {
            merchant.setBusinessName(updateDTO.getBusinessName());
        }
        if (updateDTO.getIndustry() != null) {
            merchant.setIndustry(updateDTO.getIndustry());
        }
        if (updateDTO.getCurrency() != null) {
            merchant.setCurrency(updateDTO.getCurrency());
        }
        if (updateDTO.getCountry() != null) {
            merchant.setCountry(updateDTO.getCountry());
        }
        if (updateDTO.getStatus() != null) {
            merchant.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getUpdatedBy() != null) {
            merchant.setUpdatedBy(updateDTO.getUpdatedBy());
        }
    }

    /**
     * Store请求信息转实体
     */
    public static Store toStoreEntity(MerchantRequestDTO.StoreInfo storeInfo, String merchantId) {
        if (storeInfo == null) {
            return null;
        }

        return Store.builder()
                .merchantId(merchantId)
                .storeName(storeInfo.getStoreName())
                .address(storeInfo.getAddress())
                .timezone(storeInfo.getTimezone())
                .status("ACTIVE")
                .currency("USD")
                .isDeleted(false)
                .build();
    }
}
