package com.safebox.back.rpi.repository;

import com.safebox.back.rpi.entity.Rpi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RpiRepository extends JpaRepository<Rpi, String> {
    Optional<Rpi> findByUser_Id(String userId);
}
