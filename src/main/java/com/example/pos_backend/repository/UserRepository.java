package com.example.pos_backend.repository;

import com.example.pos_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    User findByUsername(String username);
}
