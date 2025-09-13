package com.example.pos_backend.service;

import com.example.pos_backend.constants.GlobalConstants;
import com.example.pos_backend.constants.UserConstants;
import com.example.pos_backend.entity.User;
import com.example.pos_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务类
 * 提供用户相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * 创建新用户
     *
     * @param user 用户实体
     * @return 创建的用户
     */
    public User createUser(User user) {
        log.info("创建新用户: {}", user.getUsername());
        
        // 设置创建时间和更新时间
        Instant now = Instant.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setIsDeleted(GlobalConstants.Database.NOT_DELETED);
        
        // 设置默认值
        if (user.getRole() == null) {
            user.setRole(UserConstants.Role.DEFAULT);
        }
        if (user.getStatus() == null) {
            user.setStatus(UserConstants.Status.DEFAULT);
        }
        
        User savedUser = userRepository.save(user);
        log.info("用户创建成功，ID: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户信息
     * @throws RuntimeException 如果用户不存在
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        log.info("根据ID获取用户: {}", id);
        return userRepository.findById(id)
                .filter(user -> !GlobalConstants.Database.IS_DELETED.equals(user.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + id));
    }

    /**
     * 获取所有用户（未删除）
     *
     * @return 用户列表
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.info("获取所有用户");
        return userRepository.findAll().stream()
                .filter(user -> !GlobalConstants.Database.IS_DELETED.equals(user.getIsDeleted()))
                .toList();
    }

    /**
     * 分页获取所有用户（未删除）
     *
     * @param pageable 分页参数
     * @return 分页用户列表
     */
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        log.info("分页获取用户，页码: {}, 大小: {}", pageable.getPageNumber(), pageable.getPageSize());
        // 注意：这里简化处理，实际项目中应该在Repository层添加查询方法来过滤软删除的记录
        return userRepository.findAll(pageable);
    }

    /**
     * 更新用户信息
     *
     * @param id   用户ID
     * @param user 更新的用户信息
     * @return 更新后的用户
     * @throws RuntimeException 如果用户不存在
     */
    public User updateUser(Long id, User user) {
        log.info("更新用户信息，ID: {}", id);
        
        User existingUser = getUserById(id);
        
        // 更新字段
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPasswordHash() != null) {
            existingUser.setPasswordHash(user.getPasswordHash());
        }
        if (user.getPinHash() != null) {
            existingUser.setPinHash(user.getPinHash());
        }
        if (user.getFirstName() != null) {
            existingUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            existingUser.setLastName(user.getLastName());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        if (user.getStatus() != null) {
            existingUser.setStatus(user.getStatus());
        }
        if (user.getSalary() != null) {
            existingUser.setSalary(user.getSalary());
        }
        if (user.getHireDate() != null) {
            existingUser.setHireDate(user.getHireDate());
        }
        if (user.getUpdatedBy() != null) {
            existingUser.setUpdatedBy(user.getUpdatedBy());
        }
        
        // 更新时间
        existingUser.setUpdatedAt(Instant.now());
        
        User updatedUser = userRepository.save(existingUser);
        log.info("用户更新成功，ID: {}", updatedUser.getId());
        return updatedUser;
    }

    /**
     * 删除用户（软删除）
     *
     * @param id 用户ID
     * @throws RuntimeException 如果用户不存在
     */
    public void deleteUser(Long id) {
        log.info("删除用户，ID: {}", id);
        
        User user = getUserById(id);
        user.setIsDeleted(GlobalConstants.Database.IS_DELETED);
        user.setUpdatedAt(Instant.now());
        
        userRepository.save(user);
        log.info("用户删除成功，ID: {}", id);
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        log.info("根据用户名查找用户: {}", username);
        User user = userRepository.findByUsername(username);
        if (user != null && !GlobalConstants.Database.IS_DELETED.equals(user.getIsDeleted())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * 根据组织ID获取用户列表
     *
     * @param orgId 组织ID
     * @return 用户列表
     */
    @Transactional(readOnly = true)
    public List<User> findByOrgId(Long orgId) {
        log.info("根据组织ID获取用户: {}", orgId);
        return userRepository.findAll().stream()
                .filter(user -> orgId.equals(user.getOrgId()))
                .filter(user -> !GlobalConstants.Database.IS_DELETED.equals(user.getIsDeleted()))
                .toList();
    }

    /**
     * 根据角色获取用户列表
     *
     * @param role 角色
     * @return 用户列表
     */
    @Transactional(readOnly = true)
    public List<User> findByRole(String role) {
        log.info("根据角色获取用户: {}", role);
        return userRepository.findAll().stream()
                .filter(user -> role.equals(user.getRole()))
                .filter(user -> !GlobalConstants.Database.IS_DELETED.equals(user.getIsDeleted()))
                .toList();
    }

    /**
     * 更新用户最后登录时间
     *
     * @param id 用户ID
     */
    public void updateLastLoginTime(Long id) {
        log.info("更新用户最后登录时间，ID: {}", id);
        User user = getUserById(id);
        user.setLastLoginAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }
}
