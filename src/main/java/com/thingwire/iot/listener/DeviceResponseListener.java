package com.thingwire.iot.listener;

import com.thingwire.iot.entity.Device;
import com.thingwire.iot.repository.DeviceRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceResponseListener {
    private static final Logger logger = LoggerFactory.getLogger(DeviceResponseListener.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @KafkaListener(topics = "thingwire.devices.responses", groupId = "thingwire-consumer-group")
    public void consumeDeviceResponse(ConsumerRecord<String, String> record) {
        logger.info("Received device response: Key={}, Value={}", record.key(), record.value());

        String deviceId = record.key();
        String newStatus = record.value(); // Expected values: "ONLINE", "OFFLINE", "ERROR"

        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            try {
                device.setStatus(Device.Status.valueOf(newStatus.toUpperCase())); // Update status
                deviceRepository.save(device);
                logger.info("Updated device {} status to {}", deviceId, newStatus);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid status received: {}", newStatus);
            }
        } else {
            logger.warn("Device with ID {} not found in database.", deviceId);
        }
    }
}
