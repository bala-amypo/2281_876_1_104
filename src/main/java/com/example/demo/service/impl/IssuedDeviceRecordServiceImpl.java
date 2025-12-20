package com.example.demo.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.IssuedDeviceRecord;
import com.example.demo.repository.DeviceCatalogItemRepository;
import com.example.demo.repository.EmployeeProfileRepository;
import com.example.demo.repository.IssuedDeviceRecordRepository;
import com.example.demo.service.IssuedDeviceRecordService;

@Service
public class IssuedDeviceRecordServiceImpl implements IssuedDeviceRecordService {

    private final IssuedDeviceRecordRepository issuedRepo;
    private final EmployeeProfileRepository employeeRepo;
    private final DeviceCatalogItemRepository deviceRepo;

    public IssuedDeviceRecordServiceImpl(
            IssuedDeviceRecordRepository issuedRepo,
            EmployeeProfileRepository employeeRepo,
            DeviceCatalogItemRepository deviceRepo) {

        this.issuedRepo = issuedRepo;
        this.employeeRepo = employeeRepo;
        this.deviceRepo = deviceRepo;
    }

    // ✅ Issue device
    @Override
    public IssuedDeviceRecord issueDevice(IssuedDeviceRecord record) {

        // Validate employee exists
        employeeRepo.findById(record.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        // Validate device exists
        deviceRepo.findById(record.getDeviceItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        // Check if device is already issued to this employee
        Optional<IssuedDeviceRecord> existingRecord =
                issuedRepo.findByEmployeeIdAndDeviceItemIdAndStatus(
                        record.getEmployeeId(),
                        record.getDeviceItemId(),
                        "ISSUED"
                );

        if (existingRecord.isPresent()) {
            throw new IllegalStateException("Device already issued to this employee");
        }

        // Set issued date and status
        record.setIssuedDate(LocalDate.now());
        record.setReturnedDate(null);
        record.setStatus("ISSUED");

        return issuedRepo.save(record);
    }

    // ✅ Return device
    @Override
    public IssuedDeviceRecord returnDevice(Long recordId) {

        IssuedDeviceRecord record = issuedRepo.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Issued record not found"));

        if ("RETURNED".equals(record.getStatus())) {
            throw new IllegalStateException("already returned");
        }

        record.setStatus("RETURNED");
        record.setReturnedDate(LocalDate.now());

        return issuedRepo.save(record);
    }

    // ✅ Get all active (ISSUED) devices for an employee
    @Override
    public List<IssuedDeviceRecord> getActiveByEmployeeId(Long employeeId) {
        return issuedRepo.findByEmployeeIdAndStatus(employeeId, "ISSUED");
    }

    // ✅ Get all devices (regardless of status) for an employee
    @Override
    public List<IssuedDeviceRecord> getByEmployeeId(Long employeeId) {
        return issuedRepo.findByEmployeeId(employeeId);
    }

    // ✅ Get record by ID
    @Override
    public IssuedDeviceRecord getById(Long id) {
        return issuedRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issued record not found"));
    }

    // ✅ Count active devices for employee
    @Override
    public long countActiveDevicesForEmployee(Long employeeId) {
        return issuedRepo.countByEmployeeIdAndStatus(employeeId, "ISSUED");
    }
}
