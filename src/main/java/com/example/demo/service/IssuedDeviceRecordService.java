package com.example.demo.service;

import java.util.List;

import com.example.demo.model.IssuedDeviceRecord;

public interface IssuedDeviceRecordService {

    IssuedDeviceRecord issueDevice(Long employeeId, Long deviceItemId);

    IssuedDeviceRecord returnDevice(Long issuedDeviceRecordId);

    IssuedDeviceRecord getById(Long id);

    List<IssuedDeviceRecord> getAll();

    List<IssuedDeviceRecord> getByEmployee(Long employeeId);
}
