package com.example.pos_backend.service;

import com.example.pos_backend.dto.UserRegisterRequest;
import com.example.pos_backend.entity.User;
import com.example.pos_backend.enums.UserRole;
import com.example.pos_backend.enums.UserStatus;
import com.example.pos_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(UserRegisterRequest request) {
        User user = User.builder()
                .orgId(request.getOrgId())
                .storeId(request.getStoreId())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword()) // ⚠️ 后续换成加密存储
                .role(UserRole.STAFF)
                .status(UserStatus.ACTIVE)
                .build();
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password)) // ⚠️ 后续换成加密验证
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
    }
}
