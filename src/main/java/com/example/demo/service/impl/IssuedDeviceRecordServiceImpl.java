package com.example.demo.service.impl;

import java.time.LocalDate;
import java.util.List;

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

        employeeRepo.findById(record.getEmployeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        deviceRepo.findById(record.getDeviceItemId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Device not found"));

        record.setIssuedDate(LocalDate.now());
        record.setReturnedDate(null);
        record.setStatus("ISSUED");

        return issuedRepo.save(record);
    }

    // ✅ Return device
    @Override
    public IssuedDeviceRecord returnDevice(Long recordId) {

        IssuedDeviceRecord record = issuedRepo.findById(recordId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Issued record not found"));

        if ("RETURNED".equals(record.getStatus())) {
            throw new IllegalStateException("already returned");
        }

        record.setStatus("RETURNED");
        record.setReturnedDate(LocalDate.now());

        return issuedRepo.save(record);
    }

    // ✅ Get issued devices by employee
    @Override
    public List<IssuedDeviceRecord> getIssuedDevicesByEmployee(Long employeeId) {
        return issuedRepo.findByEmployeeId(employeeId);
    }

    // ✅ Required by interface
    @Override
    public IssuedDeviceRecord getById(Long id) {
        return issuedRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Issued record not found"));
    }
}
