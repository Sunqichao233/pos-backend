package com.example.pos_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 商家响应DTO - Square风格
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseDTO {

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("location_id")
    private String locationId;

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

    private String message;
}
