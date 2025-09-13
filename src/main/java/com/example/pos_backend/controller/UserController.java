package com.example.pos_backend.controller;

import com.example.pos_backend.constants.GlobalConstants;
import com.example.pos_backend.constants.UserConstants;
import com.example.pos_backend.dto.UserMapper;
import com.example.pos_backend.dto.UserRequestDTO;
import com.example.pos_backend.dto.UserResponseDTO;
import com.example.pos_backend.dto.UserUpdateDTO;
import com.example.pos_backend.entity.User;
import com.example.pos_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户控制器
 * 提供用户相关的REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * 创建新用户
     *
     * @param userRequestDTO 用户请求信息
     * @return 创建的用户信息
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        log.info("接收创建用户请求: {}", userRequestDTO.getUsername());
        try {
            User user = userMapper.toEntity(userRequestDTO);
            User createdUser = userService.createUser(user);
            UserResponseDTO responseDTO = userMapper.toResponseDTO(createdUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("创建用户失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("接收获取用户请求，ID: {}", id);
        try {
            User user = userService.getUserById(id);
            UserResponseDTO responseDTO = userMapper.toResponseDTO(user);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            log.error("获取用户失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取所有用户（支持分页）
     *
     * @param pageable 分页参数（可选）
     * @return 用户列表
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @PageableDefault(size = GlobalConstants.Database.DEFAULT_PAGE_SIZE) Pageable pageable,
            @RequestParam(value = "page", defaultValue = "false") boolean enablePaging) {
        log.info("接收获取所有用户请求，分页: {}", enablePaging);
        
        try {
            if (enablePaging) {
                Page<User> users = userService.getAllUsers(pageable);
                Page<UserResponseDTO> responseDTOs = users.map(userMapper::toResponseDTO);
                return ResponseEntity.ok(responseDTOs);
            } else {
                List<User> users = userService.getAllUsers();
                List<UserResponseDTO> responseDTOs = userMapper.toResponseDTOList(users);
                return ResponseEntity.ok(responseDTOs);
            }
        } catch (Exception e) {
            log.error("获取用户列表失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 更新用户信息
     *
     * @param id            用户ID
     * @param userUpdateDTO 更新的用户信息
     * @return 更新后的用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("接收更新用户请求，ID: {}", id);
        try {
            User updateUser = userMapper.toEntityFromUpdateDTO(userUpdateDTO);
            User updatedUser = userService.updateUser(id, updateUser);
            UserResponseDTO responseDTO = userMapper.toResponseDTO(updatedUser);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            log.error("更新用户失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("更新用户失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 删除用户（软删除）
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        log.info("接收删除用户请求，ID: {}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", GlobalConstants.ResponseMessage.DELETED, "id", id.toString()));
        } catch (RuntimeException e) {
            log.error("删除用户失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("删除用户失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        log.info("接收根据用户名获取用户请求: {}", username);
        Optional<User> user = userService.findByUsername(username);
        return user.map(u -> ResponseEntity.ok(userMapper.toResponseDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据组织ID获取用户列表
     *
     * @param orgId 组织ID
     * @return 用户列表
     */
    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByOrgId(@PathVariable Long orgId) {
        log.info("接收根据组织ID获取用户请求: {}", orgId);
        try {
            List<User> users = userService.findByOrgId(orgId);
            List<UserResponseDTO> responseDTOs = userMapper.toResponseDTOList(users);
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            log.error("根据组织ID获取用户失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据角色获取用户列表
     *
     * @param role 角色
     * @return 用户列表
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable String role) {
        log.info("接收根据角色获取用户请求: {}", role);
        try {
            List<User> users = userService.findByRole(role);
            List<UserResponseDTO> responseDTOs = userMapper.toResponseDTOList(users);
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            log.error("根据角色获取用户失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 更新用户最后登录时间
     *
     * @param id 用户ID
     * @return 更新结果
     */
    @PatchMapping("/{id}/login")
    public ResponseEntity<Map<String, String>> updateLastLoginTime(@PathVariable Long id) {
        log.info("接收更新用户登录时间请求，ID: {}", id);
        try {
            userService.updateLastLoginTime(id);
            return ResponseEntity.ok(Map.of("message", GlobalConstants.ResponseMessage.UPDATED, "id", id.toString()));
        } catch (RuntimeException e) {
            log.error("更新登录时间失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("更新登录时间失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取用户统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        log.info("接收获取用户统计信息请求");
        try {
            List<User> allUsers = userService.getAllUsers();
            long totalUsers = allUsers.size();
            long activeUsers = allUsers.stream()
                    .filter(user -> UserConstants.Status.ACTIVE.equals(user.getStatus()))
                    .count();
            long staffCount = allUsers.stream()
                    .filter(user -> UserConstants.Role.STAFF.equals(user.getRole()))
                    .count();
            long managerCount = allUsers.stream()
                    .filter(user -> UserConstants.Role.MANAGER.equals(user.getRole()))
                    .count();
            long ownerCount = allUsers.stream()
                    .filter(user -> UserConstants.Role.OWNER.equals(user.getRole()))
                    .count();

            Map<String, Object> stats = Map.of(
                    "totalUsers", totalUsers,
                    "activeUsers", activeUsers,
                    "staffCount", staffCount,
                    "managerCount", managerCount,
                    "ownerCount", ownerCount
            );

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("获取用户统计信息失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
