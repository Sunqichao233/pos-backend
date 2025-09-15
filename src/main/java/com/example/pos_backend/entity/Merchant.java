package com.example.pos_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "merchants", schema = "posdb")
public class Merchant {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "CHAR(36)")
    private String id;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Size(max = 255)
    @NotNull
    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Size(max = 100)
    @NotNull
    @Column(name = "industry", nullable = false, length = 100)
    private String industry;

    @NotNull
    @ColumnDefault("'USD'")
    @Column(name = "currency", nullable = false, columnDefinition = "CHAR(3)")
    private String currency;

    @NotNull
    @ColumnDefault("'US'")
    @Column(name = "country", nullable = false, columnDefinition = "CHAR(2)")
    private String country;

    @Size(max = 50)
    @ColumnDefault("'ACTIVE'")
    @Column(name = "status", length = 50)
    private String status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "created_by", columnDefinition = "CHAR(36)")
    private String createdBy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by", columnDefinition = "CHAR(36)")
    private String updatedBy;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

}