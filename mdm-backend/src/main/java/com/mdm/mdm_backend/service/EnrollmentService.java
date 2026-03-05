package com.mdm.mdm_backend.service;

import com.mdm.mdm_backend.model.dto.EnrollRequest;
import com.mdm.mdm_backend.model.entity.Device;
import com.mdm.mdm_backend.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {

    private final DeviceRepository deviceRepository;

    public Device enrollDevice(EnrollRequest request) {
        if (deviceRepository.existsByDeviceId(request.getDeviceId())) {
            log.info("Device {} already enrolled, updating...", request.getDeviceId());
            Device existing = deviceRepository.findByDeviceId(request.getDeviceId()).get();
            existing.setStatus("ACTIVE");
            return deviceRepository.save(existing);
        }

        Device device = Device.builder()
                .deviceId(request.getDeviceId())
                .enrollmentToken(request.getEnrollmentToken())
                .enrollmentMethod(request.getEnrollmentMethod())
                .enrolledAt(LocalDateTime.now())
                .status("ACTIVE")
                .build();

        log.info("Enrolling new device: {}", request.getDeviceId());
        return deviceRepository.save(device);
    }
}