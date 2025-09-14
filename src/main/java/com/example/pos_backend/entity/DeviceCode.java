package com.example.pos_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "device_codes", schema = "posdb")
public class DeviceCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_code_id", nullable = false)
    private Long id;

    @Size(max = 12)
    @NotNull
    @Column(name = "device_code", nullable = false, length = 12, unique = true)
    private String deviceCode;

    @Column(name = "device_id")
    private Long deviceId;

    @Size(max = 255)
    @Column(name = "device_fingerprint", length = 255)
    private String deviceFingerprint;

    @ColumnDefault("0")
    @Column(name = "activation_attempts")
    private Integer activationAttempts;

    @ColumnDefault("3")
    @Column(name = "max_attempts")
    private Integer maxAttempts;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'UNUSED'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @Column(name = "bound_at")
    private Instant boundAt;

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