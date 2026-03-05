package com.mdm.mdm_backend.repository;

import com.mdm.mdm_backend.model.entity.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, Long> {

    List<DeviceInfo> findByDeviceIdOrderByCollectedAtDesc(String deviceId);
}