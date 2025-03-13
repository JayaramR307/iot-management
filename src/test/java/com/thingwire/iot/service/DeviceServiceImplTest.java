package com.thingwire.iot.service;

import com.thingwire.iot.entity.Device;
import com.thingwire.iot.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceImplTest {
    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    private Device testDevice;
    private String deviceId;
    private Map<String, Object> metadata;

    @BeforeEach
    void setUp() {
        deviceId = UUID.randomUUID().toString();
        testDevice = new Device();
        testDevice.setId(deviceId);
        testDevice.setName("Device1");
        testDevice.setStatus(Device.Status.OFFLINE);
        testDevice.setLastSeen(LocalDateTime.now());
        testDevice.setMetadata(metadata);
    }

    @Test
    void shouldRegisterDevice() {
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);
        Device savedDevice = deviceService.registerDevice(testDevice);

        assertNotNull(savedDevice);
        assertEquals(Device.Status.OFFLINE, savedDevice.getStatus());
        verify(deviceRepository, times(1)).save(any(Device.class));
        verify(kafkaTemplate, times(1)).send(eq("thingwire.devices.events"), contains("Device Registered"));
    }

    @Test
    void shouldFetchDeviceById() {
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(testDevice));

        Optional<Device> fetchedDevice = deviceService.getDeviceById(deviceId);

        assertTrue(fetchedDevice.isPresent());
        assertEquals(deviceId, fetchedDevice.get().getId());
    }

    @Test
    void shouldUpdateDevice() {
        Map<String, Object> updatedMetadata = new HashMap<>();
        updatedMetadata.put("location", "Office");
        updatedMetadata.put("humidity", 60);

        Device updatedDevice = new Device();
        updatedDevice.setName("UpdatedDevice");
        updatedDevice.setMetadata(updatedMetadata);

        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(testDevice));
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Device result = deviceService.updateDevice(deviceId, updatedDevice);

        assertNotNull(result);
        assertEquals("UpdatedDevice", result.getName());
        assertEquals("Office", result.getMetadata().get("location"));
        assertEquals(60, result.getMetadata().get("humidity"));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingDevice() {
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> deviceService.updateDevice(deviceId, testDevice));
    }

    @Test
    void shouldDeleteDevice() {
        doNothing().when(deviceRepository).deleteById(deviceId);

        deviceService.deleteDevice(deviceId);

        verify(deviceRepository, times(1)).deleteById(deviceId);
    }

    @Test
    void shouldSendCommandToDevice() {
        deviceService.sendCommandToDevice(deviceId, "RESTART");

        verify(kafkaTemplate, times(1)).send(eq("thingwire.devices.commands"), contains("Command sent to device"));
    }

    @Test
    void shouldFetchAllDevices() {
        List<Device> devices = Arrays.asList(testDevice, new Device());
        when(deviceRepository.findAll()).thenReturn(devices);

        List<Device> result = deviceService.getAllDevices();

        assertEquals(2, result.size());
    }
}
