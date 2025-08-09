package com.safebox.back.rpi.entity;

import com.safebox.back.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "rpi")
public class Rpi {
    @Id
    @Column(name = "rpi_id", columnDefinition = "CHAR(36)")
    private String rpiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
