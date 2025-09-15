package com.example.pos_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "stores", schema = "posdb")
public class Store {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "CHAR(36)")
    private String id;

    @NotNull
    @Column(name = "merchant_id", nullable = false, columnDefinition = "CHAR(36)")
    private String merchantId;

    @Size(max = 255)
    @NotNull
    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Size(max = 64)
    @ColumnDefault("'UTC'")
    @Column(name = "timezone", length = 64)
    private String timezone;

    @Size(max = 50)
    @ColumnDefault("'ACTIVE'")
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "tax_rate", precision = 5, scale = 4)
    @ColumnDefault("0.0000")
    private BigDecimal taxRate;

    @Size(max = 3)
    @ColumnDefault("'USD'")
    @Column(name = "currency", columnDefinition = "CHAR(3)")
    private String currency;

    @Column(name = "business_hours", columnDefinition = "JSON")
    private String businessHours;

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