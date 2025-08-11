package com.safebox.back.rpi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stolen_deliveries")
public class StolenDelivery {

    @Id
    @Column(name = "delivery_id", nullable = false, columnDefinition = "char(36)")
    private String deliveryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rpi_id", nullable = false)
    private Rpi rpi;

    @Column(name = "arrived_at", nullable = false, columnDefinition = "datetime(6)")
    private LocalDateTime arrivedAt;

    @Column(name = "retrieved_at", columnDefinition = "datetime(6)")
    private LocalDateTime retrievedAt;
}