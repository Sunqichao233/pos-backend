package com.example.pos_backend.dto;

import com.example.pos_backend.constants.UserConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = UserConstants.Validation.MIN_USERNAME_LENGTH, 
          max = UserConstants.Validation.MAX_USERNAME_LENGTH, 
          message = "用户名长度必须在{min}-{max}个字符之间")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = UserConstants.Validation.MIN_PASSWORD_LENGTH, 
          max = UserConstants.Validation.MAX_PASSWORD_LENGTH, 
          message = "密码长度必须在{min}-{max}个字符之间")
    private String password;

    /**
     * 记住我（可选）
     */
    private Boolean rememberMe = false;
}
