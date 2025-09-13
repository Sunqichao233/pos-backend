package com.example.pos_backend.dto;

import com.example.pos_backend.constants.GlobalConstants;
import com.example.pos_backend.constants.UserConstants;
import com.example.pos_backend.entity.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 DTO 转换工具类
 * 负责 Entity 和 DTO 之间的转换
 */
@Component
public class UserMapper {

    /**
     * 将 UserRequestDTO 转换为 User 实体
     *
     * @param requestDTO 请求 DTO
     * @return User 实体
     */
    public User toEntity(UserRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        return User.builder()
                .orgId(requestDTO.getOrgId())
                .username(requestDTO.getUsername())
                .email(requestDTO.getEmail())
                .passwordHash(requestDTO.getPasswordHash())
                .pinHash(requestDTO.getPinHash())
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .role(requestDTO.getRole() != null ? requestDTO.getRole() : UserConstants.Role.DEFAULT)
                .status(requestDTO.getStatus() != null ? requestDTO.getStatus() : UserConstants.Status.DEFAULT)
                .salary(requestDTO.getSalary())
                .hireDate(requestDTO.getHireDate())
                .createdBy(requestDTO.getCreatedBy())
                .updatedBy(requestDTO.getUpdatedBy())
                .isDeleted(GlobalConstants.Database.NOT_DELETED)
                .build();
    }

    /**
     * 将 User 实体转换为 UserResponseDTO
     *
     * @param user User 实体
     * @return 响应 DTO
     */
    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .orgId(user.getOrgId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .status(user.getStatus())
                .salary(user.getSalary())
                .hireDate(user.getHireDate())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    /**
     * 将 User 实体列表转换为 UserResponseDTO 列表
     *
     * @param users User 实体列表
     * @return 响应 DTO 列表
     */
    public List<UserResponseDTO> toResponseDTOList(List<User> users) {
        if (users == null) {
            return null;
        }

        return users.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 使用 UserUpdateDTO 更新 User 实体
     *
     * @param user      要更新的 User 实体
     * @param updateDTO 更新 DTO
     */
    public void updateEntityFromDTO(User user, UserUpdateDTO updateDTO) {
        if (user == null || updateDTO == null) {
            return;
        }

        if (updateDTO.getUsername() != null) {
            user.setUsername(updateDTO.getUsername());
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPasswordHash() != null) {
            user.setPasswordHash(updateDTO.getPasswordHash());
        }
        if (updateDTO.getPinHash() != null) {
            user.setPinHash(updateDTO.getPinHash());
        }
        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getRole() != null) {
            user.setRole(updateDTO.getRole());
        }
        if (updateDTO.getStatus() != null) {
            user.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getSalary() != null) {
            user.setSalary(updateDTO.getSalary());
        }
        if (updateDTO.getHireDate() != null) {
            user.setHireDate(updateDTO.getHireDate());
        }
        if (updateDTO.getUpdatedBy() != null) {
            user.setUpdatedBy(updateDTO.getUpdatedBy());
        }

        // 更新时间
        user.setUpdatedAt(Instant.now());
    }

    /**
     * 将 UserUpdateDTO 转换为 User 实体（用于部分更新）
     *
     * @param updateDTO 更新 DTO
     * @return User 实体
     */
    public User toEntityFromUpdateDTO(UserUpdateDTO updateDTO) {
        if (updateDTO == null) {
            return null;
        }

        return User.builder()
                .username(updateDTO.getUsername())
                .email(updateDTO.getEmail())
                .passwordHash(updateDTO.getPasswordHash())
                .pinHash(updateDTO.getPinHash())
                .firstName(updateDTO.getFirstName())
                .lastName(updateDTO.getLastName())
                .role(updateDTO.getRole())
                .status(updateDTO.getStatus())
                .salary(updateDTO.getSalary())
                .hireDate(updateDTO.getHireDate())
                .updatedBy(updateDTO.getUpdatedBy())
                .build();
    }
}
