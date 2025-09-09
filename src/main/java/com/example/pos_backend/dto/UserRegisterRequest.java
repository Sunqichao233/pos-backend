package com.example.pos_backend.dto;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private Long orgId;
    private Long storeId;
    private String username;
    private String email;
    private String password;
}
