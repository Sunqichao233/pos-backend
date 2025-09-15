package com.example.pos_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 商家登录响应DTO - OAuth2风格
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantLoginResponseDTO {

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("location_id")
    private String locationId;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    @Builder.Default
    private String tokenType = "Bearer";

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_expires_in")
    private Long refreshExpiresIn;

    @JsonProperty("session_id")
    private String sessionId;

    // 商家基本信息
    @JsonProperty("merchant_info")
    private MerchantInfo merchantInfo;

    private String status;
    private String message;

    /**
     * 商家信息内嵌类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MerchantInfo {
        private String email;

        @JsonProperty("business_name")
        private String businessName;

        private String industry;
        private String currency;
        private String country;
        private String status;

        @JsonProperty("created_at")
        private Instant createdAt;

        @JsonProperty("updated_at")
        private Instant updatedAt;
    }
}
