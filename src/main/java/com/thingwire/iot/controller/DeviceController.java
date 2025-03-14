package com.thingwire.iot.controller;

import com.thingwire.iot.dto.CommandRequest;
import com.thingwire.iot.entity.Device;
import com.thingwire.iot.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<Device> registerDevice(@RequestBody Device device) {
        Device savedDevice = deviceService.registerDevice(device);
        return ResponseEntity.ok(savedDevice);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable String id) {
        Optional<Device> device = deviceService.getDeviceById(id);
        return device.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable String id, @RequestBody Device device) {
        Device updatedDevice = deviceService.updateDevice(id, device);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/send-command")
    public ResponseEntity<String> sendCommand(@PathVariable String id, @RequestBody CommandRequest commandRequest) {
        boolean isDevicePresent = deviceService.getDeviceById(id).isPresent();

        if (!isDevicePresent) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found: " + id);
        }

        deviceService.sendCommandToDevice(id, commandRequest.getCommand());
        return ResponseEntity.ok("Command sent to device: " + id);
    }

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }
}
