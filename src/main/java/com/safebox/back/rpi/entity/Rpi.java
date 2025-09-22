package com.safebox.back.rpi.entity;

import com.safebox.back.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "rpi")
@NoArgsConstructor
@AllArgsConstructor
public class Rpi {
    @Id
    @Column(name = "rpi_id", columnDefinition = "CHAR(36)")
    private String rpiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "port", length = 5)
    private String port;
}
