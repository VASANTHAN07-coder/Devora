package com.mdm.mdm_backend.controller;

import com.mdm.mdm_backend.model.dto.EnrollRequest;
import com.mdm.mdm_backend.model.entity.Device;
import com.mdm.mdm_backend.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public ResponseEntity<?> enroll(@Valid @RequestBody EnrollRequest request) {
        Device device = enrollmentService.enrollDevice(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Device enrolled successfully",
                        "deviceId", device.getDeviceId(),
                        "status", device.getStatus()
                ));
    }
}