package com.example.pos_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商家更新DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantUpdateDTO {

    @Email(message = "邮箱格式不正确")
    @Size(max = 255, message = "邮箱长度不能超过255个字符")
    private String email;

    @Size(max = 255, message = "企业名称长度不能超过255个字符")
    @JsonProperty("business_name")
    private String businessName;

    @Size(max = 100, message = "行业类型长度不能超过100个字符")
    private String industry;

    @Size(max = 3, message = "币种代码长度不能超过3个字符")
    private String currency;

    @Size(max = 2, message = "国家代码长度不能超过2个字符")
    private String country;

    @Size(max = 50, message = "状态长度不能超过50个字符")
    private String status;

    @JsonProperty("updated_by")
    private String updatedBy;
}
