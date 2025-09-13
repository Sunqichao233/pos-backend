package com.example.pos_backend.dto;

import com.example.pos_backend.constants.GlobalConstants;
import com.example.pos_backend.constants.UserConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用户更新 DTO
 * 用于更新用户信息，所有字段都是可选的
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @Size(min = UserConstants.Validation.MIN_USERNAME_LENGTH, 
          max = UserConstants.Validation.MAX_USERNAME_LENGTH, 
          message = "用户名长度必须在{min}-{max}个字符之间")
    private String username;

    @Email(message = "邮箱格式不正确", regexp = UserConstants.Validation.EMAIL_PATTERN)
    @Size(max = GlobalConstants.StringLength.EMAIL, message = "邮箱长度不能超过{max}个字符")
    private String email;

    @Size(min = UserConstants.Validation.MIN_PASSWORD_LENGTH,
          max = UserConstants.Validation.MAX_PASSWORD_LENGTH, 
          message = "密码长度必须在{min}-{max}个字符之间")
    private String passwordHash;

    @Size(max = GlobalConstants.StringLength.PASSWORD, message = "PIN长度不能超过{max}个字符")
    private String pinHash;

    @Size(max = GlobalConstants.StringLength.NAME, message = "名字长度不能超过{max}个字符")
    private String firstName;

    @Size(max = GlobalConstants.StringLength.NAME, message = "姓氏长度不能超过{max}个字符")
    private String lastName;

    @Size(max = GlobalConstants.StringLength.SHORT_TEXT, message = "角色长度不能超过{max}个字符")
    private String role;

    @Size(max = GlobalConstants.StringLength.SHORT_TEXT, message = "状态长度不能超过{max}个字符")
    private String status;

    private BigDecimal salary;

    private LocalDate hireDate;

    private Long updatedBy;
}
