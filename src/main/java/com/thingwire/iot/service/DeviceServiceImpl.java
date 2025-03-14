package com.thingwire.iot.service;

import com.thingwire.iot.entity.Device;
import com.thingwire.iot.listener.DeviceResponseListener;
import com.thingwire.iot.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);
    private final DeviceRepository deviceRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String DEVICE_EVENTS_TOPIC = "thingwire.devices.events";
    private static final String DEVICE_COMMANDS_TOPIC = "thingwire.devices.commands";

    @Override
    public Device registerDevice(Device device) {
        device.setStatus(Device.Status.OFFLINE);
        device.setLastSeen(LocalDateTime.now());
        Device savedDevice = deviceRepository.save(device);

        // Publish event to Kafka
        kafkaTemplate.send(DEVICE_EVENTS_TOPIC, "Device Registered: " + savedDevice.getId());
        return savedDevice;
    }

    @Override
    public Optional<Device> getDeviceById(String id) {
        return deviceRepository.findById(id);
    }

    @Override
    public Device updateDevice(String id, Device updatedDevice) {
        return deviceRepository.findById(id).map(device -> {
            device.setName(updatedDevice.getName());
            device.setMetadata(updatedDevice.getMetadata());
            return deviceRepository.save(device);
        }).orElseThrow(() -> new RuntimeException("Device not found"));
    }

    @Override
    public void deleteDevice(String id) {
        deviceRepository.deleteById(id);
    }

    @Override
    public void sendCommandToDevice(String id, String command) {
        logger.info("Received device command: " + command + " for device id: " + id);
        kafkaTemplate.send(DEVICE_COMMANDS_TOPIC, "Command sent to device " + id + ": " + command);
    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }
}
