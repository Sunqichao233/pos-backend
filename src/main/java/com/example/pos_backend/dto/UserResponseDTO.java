package com.example.pos_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * 用户响应 DTO
 * 用于返回用户信息，不包含敏感数据如密码哈希
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;

    private Long orgId;

    private Long storeId;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String role;

    private String status;

    private BigDecimal salary;

    private LocalDate hireDate;

    private Instant lastLoginAt;

    private Instant createdAt;

    private Instant updatedAt;

    private Long createdBy;

    private Long updatedBy;
}
