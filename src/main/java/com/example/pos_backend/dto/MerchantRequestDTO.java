package com.example.pos_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商家注册请求DTO - Square风格
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRequestDTO {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 255, message = "邮箱长度不能超过255个字符")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 128, message = "密码长度必须在8-128个字符之间")
    private String password;

    @NotBlank(message = "企业名称不能为空")
    @Size(max = 255, message = "企业名称长度不能超过255个字符")
    @JsonProperty("business_name")
    private String businessName;

    @NotBlank(message = "行业类型不能为空")
    @Size(max = 100, message = "行业类型长度不能超过100个字符")
    private String industry;

    @Size(max = 3, message = "币种代码长度不能超过3个字符")
    @Builder.Default
    private String currency = "USD";

    @Size(max = 2, message = "国家代码长度不能超过2个字符")
    @Builder.Default
    private String country = "US";

    /**
     * 银行账户信息（嵌套对象）- 可选
     */
    @Valid
    @JsonProperty("bank_account")
    private BankAccountInfo bankAccount;

    /**
     * 门店信息（嵌套对象）
     */
    @Valid
    @NotNull(message = "门店信息不能为空")
    private StoreInfo store;

    /**
     * 银行账户信息内嵌类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankAccountInfo {
        @Size(max = 64, message = "银行账号长度不能超过64个字符")
        @JsonProperty("account_number")
        private String accountNumber;

        @Size(max = 64, message = "银行路由号长度不能超过64个字符")
        @JsonProperty("routing_number")
        private String routingNumber;

        @Size(max = 255, message = "账户持有人长度不能超过255个字符")
        @JsonProperty("account_holder")
        private String accountHolder;

        @Builder.Default
        @JsonProperty("account_type")
        private String accountType = "CHECKING";

        @JsonProperty("bank_name")
        private String bankName;
    }

    /**
     * 门店信息内嵌类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreInfo {
        @NotBlank(message = "门店名称不能为空")
        @Size(max = 255, message = "门店名称长度不能超过255个字符")
        @JsonProperty("store_name")
        private String storeName;

        @Size(max = 255, message = "地址长度不能超过255个字符")
        private String address;

        @Size(max = 64, message = "时区长度不能超过64个字符")
        @Builder.Default
        private String timezone = "Asia/Tokyo";
    }
}
