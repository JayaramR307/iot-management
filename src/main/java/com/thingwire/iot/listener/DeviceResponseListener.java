package com.thingwire.iot.listener;

import com.thingwire.iot.entity.Device;
import com.thingwire.iot.repository.DeviceRepository;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Optional;

@Service
public class DeviceResponseListener {
    private static final Logger logger = LoggerFactory.getLogger(DeviceResponseListener.class);
    private static final EnumSet<Device.Status> VALID_STATUSES = EnumSet.allOf(Device.Status.class);

    private final DeviceRepository deviceRepository;

    public DeviceResponseListener(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000, multiplier = 2.0)
    )
    @KafkaListener(topics = "thingwire.devices.responses", groupId = "thingwire-consumer-group")
    @Transactional
    public void consumeDeviceResponse(ConsumerRecord<String, String> record) {
        String deviceId = record.key();
        String newStatus = record.value();

        logger.info("Received device response: Key={}, Value={}", deviceId, newStatus);

        if (deviceId == null || newStatus == null || deviceId.isBlank() || newStatus.isBlank()) {
            logger.warn("Invalid message received. Device ID or status is null/blank. Skipping...");
            return;
        }

        deviceRepository.findById(deviceId).ifPresentOrElse(device -> {
            try {
                Device.Status statusEnum = Device.Status.valueOf(newStatus.toUpperCase());

                if (!VALID_STATUSES.contains(statusEnum)) {
                    logger.warn("Invalid status '{}' received. Ignoring update.", newStatus);
                    return;
                }

                device.setStatus(statusEnum);
                deviceRepository.save(device);
                logger.info("Updated device {} status to {}", deviceId, statusEnum);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid status received: '{}'. Allowed values: {}", newStatus, VALID_STATUSES, e);
                throw e;  // This will trigger a retry
            }
        }, () -> logger.warn("Device with ID '{}' not found in database. Skipping...", deviceId));
    }
}