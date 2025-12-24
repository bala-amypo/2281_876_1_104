package com.example.demo.service;

import java.util.List;

import com.example.demo.model.IssuedDeviceRecord;

public interface IssuedDeviceRecordService {

    IssuedDeviceRecord issueDevice(IssuedDeviceRecord record);

    IssuedDeviceRecord returnDevice(Long issuedDeviceRecordId);

    IssuedDeviceRecord getById(Long id);

    List<IssuedDeviceRecord> getAll();

    List<IssuedDeviceRecord> getByEmployeeId(Long employeeId);

    List<IssuedDeviceRecord> getByEmployeeIdAndStatus(Long employeeId, String status);
}
