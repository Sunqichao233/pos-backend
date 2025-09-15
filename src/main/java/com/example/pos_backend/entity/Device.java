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
@Table(name = "devices", schema = "posdb")
public class Device {
    @Id
    @Column(name = "device_id", nullable = false, columnDefinition = "CHAR(36)")
    private String id;

    @Size(max = 100)
    @NotNull
    @Column(name = "device_name", nullable = false, length = 100)
    private String deviceName;

    @Size(max = 50)
    @NotNull
    @Column(name = "device_type", nullable = false, length = 50)
    private String deviceType;

    @Size(max = 17)
    @Column(name = "mac_address", length = 17)
    private String macAddress;

    @Size(max = 15)
    @Column(name = "ip_address", length = 15)
    private String ipAddress;

    @Column(name = "last_online")
    private Instant lastOnline;

    @ColumnDefault("'OFFLINE'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "registered_at")
    private Instant registeredAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", columnDefinition = "CHAR(36)")
    private String createdBy;

    @Column(name = "updated_by", columnDefinition = "CHAR(36)")
    private String updatedBy;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

}