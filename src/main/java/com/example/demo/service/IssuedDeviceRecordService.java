package com.example.demo.service;

import java.util.List;

import com.example.demo.model.IssuedDeviceRecord;

public interface IssuedDeviceRecordService {
    IssuedDeviceRecord issueDevice(IssuedDeviceRecord record);
    IssuedDeviceRecord returnDevice(Long id);
    List<IssuedDeviceRecord> getByEmployeeId(Long employeeId);
    IssuedDeviceRecord getById(Long id);
}

