package com.mdm.mdm_backend.repository;

import com.mdm.mdm_backend.model.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByDeviceId(String deviceId);

    boolean existsByDeviceId(String deviceId);

    boolean existsByDeviceIdAndStatus(String deviceId, String status);

    long countByStatus(String status);

    List<Device> findByStatus(String status);

    @Query("SELECT d FROM Device d WHERE d.status = 'ACTIVE' AND " +
           "(d.lastHeartbeat IS NULL OR d.lastHeartbeat < :threshold)")
    List<Device> findStaleActiveDevices(@Param("threshold") LocalDateTime threshold);
}