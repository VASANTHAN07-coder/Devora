package com.mdm.mdm_backend.controller;

import com.mdm.mdm_backend.model.dto.DeviceResponse;
import com.mdm.mdm_backend.model.entity.Device;
import com.mdm.mdm_backend.repository.DeviceRepository;
import com.mdm.mdm_backend.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class DeviceController {

    private final EnrollmentService enrollmentService;
    private final DeviceRepository deviceRepository;

    @GetMapping("/devices")
    public ResponseEntity<List<DeviceResponse>> getAllDevices() {
        return ResponseEntity.ok(enrollmentService.getAllDevicesAsResponse());
    }

    @GetMapping("/devices/{deviceId}")
    public ResponseEntity<DeviceResponse> getDevice(@PathVariable String deviceId) {
        return enrollmentService.getDeviceAsResponse(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/devices/{deviceId}")
    public ResponseEntity<Map<String, String>> deleteDevice(@PathVariable String deviceId) {
        try {
            boolean deleted = enrollmentService.deleteDeviceWithAllData(deviceId);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Device not found"));
            }

            return ResponseEntity.ok(Map.of("message", "Device deleted successfully"));
        } catch (Exception ex) {
            log.error("Failed to delete device {}", deviceId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to delete device due to server error"));
        }
    }

    @GetMapping("/devices/check/{deviceId}")
    public ResponseEntity<DeviceResponse> checkDevice(@PathVariable String deviceId) {
        return enrollmentService.getDeviceAsResponse(deviceId)
                .filter(device -> "ACTIVE".equalsIgnoreCase(device.getStatus()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Called by the enrolled device every 15 minutes via SyncWorker.
     * Updates lastHeartbeat to now and ensures status is ACTIVE.
     * When the MDM app is uninstalled, this stops being called and the
     * DeviceStatusScheduler will eventually mark the device as INACTIVE.
     */
    @PostMapping("/devices/{deviceId}/heartbeat")
    public ResponseEntity<Map<String, String>> heartbeat(@PathVariable String deviceId) {
        return deviceRepository.findByDeviceId(deviceId)
                .map(device -> {
                    device.setLastHeartbeat(LocalDateTime.now());
                    if (!"ACTIVE".equalsIgnoreCase(device.getStatus())) {
                        device.setStatus("ACTIVE");
                        log.info("Device {} came back online, marked ACTIVE", deviceId);
                    }
                    deviceRepository.save(device);
                    return ResponseEntity.ok(Map.of("status", "ok"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Device not found")));
    }
}
