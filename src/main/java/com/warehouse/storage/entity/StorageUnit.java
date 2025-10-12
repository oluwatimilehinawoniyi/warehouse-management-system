package com.warehouse.storage.entity;

import com.warehouse.bookings.entity.Booking;
import com.warehouse.warehouses.entity.Warehouse;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "storage_units")
public class StorageUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Column(nullable = false)
    private String unitNumber;

    @Column(nullable = false)
    private Integer capacityKg;

    @Enumerated(EnumType.STRING)
    private StorageStatus status = StorageStatus.AVAILABLE;

    @Version
    private Integer version = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", insertable = false, updatable = false)
    private Warehouse warehouse;

    @OneToMany(mappedBy = "storageUnit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
}