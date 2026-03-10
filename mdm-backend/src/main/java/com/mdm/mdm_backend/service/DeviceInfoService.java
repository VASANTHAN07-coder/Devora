package com.mdm.mdm_backend.service;

import com.mdm.mdm_backend.model.dto.DeviceInfoRequest;
import com.mdm.mdm_backend.model.entity.Device;
import com.mdm.mdm_backend.model.entity.DeviceInfo;
import com.mdm.mdm_backend.repository.DeviceInfoRepository;
import com.mdm.mdm_backend.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceInfoService {

    private final DeviceInfoRepository deviceInfoRepository;
    private final DeviceRepository deviceRepository;

    public DeviceInfo saveDeviceInfo(DeviceInfoRequest request) {
        DeviceInfo info = DeviceInfo.builder()
                .deviceId(request.getDeviceId())
                .model(request.getModel())
                .manufacturer(request.getManufacturer())
                .osVersion(request.getOsVersion())
                .sdkVersion(request.getSdkVersion() != null ? request.getSdkVersion().toString() : null)
                .serialNumber(request.getSerialNumber())
                .imei(request.getImei())
                .deviceType(request.getDeviceType())
                .employeeId(request.getEmployeeId())
                .collectedAt(LocalDateTime.now())
                .build();

        // Also update the Device record with model/manufacturer so the admin list is accurate
        deviceRepository.findByDeviceId(request.getDeviceId()).ifPresent(device -> {
            if (request.getModel() != null) device.setDeviceModel(request.getModel());
            if (request.getManufacturer() != null) device.setManufacturer(request.getManufacturer());
            deviceRepository.save(device);
        });

        log.info("Saving device info for: {} (employee: {})", request.getDeviceId(), request.getEmployeeId());
        return deviceInfoRepository.save(info);
    }

    public List<DeviceInfo> getDeviceInfo(String deviceId) {
        return deviceInfoRepository.findByDeviceIdOrderByCollectedAtDesc(deviceId);
    }
}