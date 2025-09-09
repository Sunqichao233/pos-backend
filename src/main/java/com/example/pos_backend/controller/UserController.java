package com.example.pos_backend.controller;

import com.example.pos_backend.dto.UserRegisterRequest;
import com.example.pos_backend.dto.UserLoginRequest;
import com.example.pos_backend.entity.User;
import com.example.pos_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    // 健康检查
    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    // 用户注册
    @PostMapping("/users/register")
    public User register(@RequestBody UserRegisterRequest request) {
        return userService.register(request);
    }

    // 用户登录
    @PostMapping("/users/login")
    public User login(@RequestBody UserLoginRequest request) {
        return userService.login(request.getUsername(), request.getPassword());
    }
}
