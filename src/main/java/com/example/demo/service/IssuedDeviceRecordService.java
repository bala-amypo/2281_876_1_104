package com.example.demo.service;

import java.util.List;
import com.example.demo.model.IssuedDeviceRecord;

public interface IssuedDeviceRecordService {

    // Issue a new device
    IssuedDeviceRecord issueDevice(IssuedDeviceRecord record);

    // Return a device
    IssuedDeviceRecord returnDevice(Long recordId);

    // Get all active (ISSUED) devices for an employee
    List<IssuedDeviceRecord> getActiveByEmployeeId(Long employeeId);

    // Get all devices (any status) for an employee
    List<IssuedDeviceRecord> getByEmployeeId(Long employeeId);

    // Get a record by ID
    IssuedDeviceRecord getById(Long id);

    // Count active devices for an employee
    long countActiveDevicesForEmployee(Long employeeId);
}
