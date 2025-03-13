package com.thingwire.iot.service;

import com.thingwire.iot.entity.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceService {
    Device registerDevice(Device device);
    Optional<Device> getDeviceById(String id);
    Device updateDevice(String id, Device device);
    void deleteDevice(String id);
    void sendCommandToDevice(String id, String command);
    List<Device> getAllDevices();
}
