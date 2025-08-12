package com.safebox.back.rpi.repository;

import com.safebox.back.rpi.entity.StolenDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StolenRepository extends JpaRepository<StolenDelivery, String> {
    List<StolenDelivery> findByRpi_User_Id(String userId);

    boolean existsByDeliveryIdAndRpi_RpiIdAndRpi_User_Id(String deliveryId, String rpiId, String userId);
}
