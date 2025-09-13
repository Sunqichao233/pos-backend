package com.example.pos_backend.entity;

import com.example.pos_backend.constants.GlobalConstants;
import com.example.pos_backend.constants.UserConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users", schema = "posdb")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "org_id", nullable = false)
    private Long orgId;

    @Size(min = UserConstants.Validation.MIN_USERNAME_LENGTH, 
          max = UserConstants.Validation.MAX_USERNAME_LENGTH)
    @NotNull
    @Column(name = "username", nullable = false, length = GlobalConstants.StringLength.USERNAME)
    private String username;

    @Size(max = GlobalConstants.StringLength.EMAIL)
    @NotNull
    @Column(name = "email", nullable = false, length = GlobalConstants.StringLength.EMAIL)
    private String email;

    @Size(min = UserConstants.Validation.MIN_PASSWORD_LENGTH,
          max = UserConstants.Validation.MAX_PASSWORD_LENGTH)
    @NotNull
    @Column(name = "password_hash", nullable = false, length = GlobalConstants.StringLength.PASSWORD)
    private String passwordHash;

    @Size(max = GlobalConstants.StringLength.PASSWORD)
    @Column(name = "pin_hash", length = GlobalConstants.StringLength.PASSWORD)
    private String pinHash;

    @Size(max = GlobalConstants.StringLength.NAME)
    @Column(name = "first_name", length = GlobalConstants.StringLength.NAME)
    private String firstName;

    @Size(max = GlobalConstants.StringLength.NAME)
    @Column(name = "last_name", length = GlobalConstants.StringLength.NAME)
    private String lastName;

    @NotNull
    @ColumnDefault("'" + UserConstants.Role.DEFAULT + "'")
    @Column(name = "role", nullable = false, length = GlobalConstants.StringLength.SHORT_TEXT)
    private String role;

    @NotNull
    @ColumnDefault("'" + UserConstants.Status.DEFAULT + "'")
    @Column(name = "status", nullable = false, length = GlobalConstants.StringLength.SHORT_TEXT)
    private String status;

    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

}