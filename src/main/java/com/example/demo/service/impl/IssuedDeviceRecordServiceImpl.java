package com.example.demo.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.DeviceCatalogItem;
import com.example.demo.model.EmployeeProfile;
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

    // ✅ MATCHES INTERFACE
    @Override
    public IssuedDeviceRecord issueDevice(IssuedDeviceRecord record) {

        EmployeeProfile employee = employeeRepo.findById(record.getEmployeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        if (!Boolean.TRUE.equals(employee.getActive())) {
            throw new BadRequestException("Employee is inactive");
        }

        DeviceCatalogItem device = deviceRepo.findById(record.getDeviceItemId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Device not found"));

        if (!Boolean.TRUE.equals(device.getActive())) {
            throw new BadRequestException("Device is inactive");
        }

        record.setIssuedDate(LocalDate.now());
        record.setReturnedDate(null);
        record.setStatus("ISSUED");

        return issuedRepo.save(record);
    }

    // ✅ MATCHES INTERFACE
    @Override
    public IssuedDeviceRecord returnDevice(Long issuedDeviceRecordId) {

        IssuedDeviceRecord record = issuedRepo.findById(issuedDeviceRecordId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Issued record not found"));

        if ("RETURNED".equals(record.getStatus())) {
            throw new BadRequestException("Device already returned");
        }

        record.setStatus("RETURNED");
        record.setReturnedDate(LocalDate.now());

        return issuedRepo.save(record);
    }

    // ✅ MATCHES INTERFACE
    @Override
    public IssuedDeviceRecord getById(Long id) {
        return issuedRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Issued record not found"));
    }

    // ✅ MATCHES INTERFACE
    @Override
    public List<IssuedDeviceRecord> getAll() {
        return issuedRepo.findAll();
    }

    // ✅ MATCHES INTERFACE
    @Override
    public List<IssuedDeviceRecord> getByEmployeeId(Long employeeId) {
        return issuedRepo.findByEmployeeId(employeeId);
    }

    // ✅ MATCHES INTERFACE
    @Override
    public List<IssuedDeviceRecord> getByEmployeeIdAndStatus(Long employeeId, String status) {
        return issuedRepo.findByEmployeeIdAndStatus(employeeId, status);
    }
}
