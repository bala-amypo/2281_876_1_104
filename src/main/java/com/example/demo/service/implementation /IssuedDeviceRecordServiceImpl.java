package com.example.demo.service.implementation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

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

    // ✅ Create issuance record
    @Override
    public IssuedDeviceRecord issueDevice(IssuedDeviceRecord record) {

        // 1️⃣ Validate employee exists
        EmployeeProfile employee = employeeRepo.findById(record.getEmployeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        // 2️⃣ Validate device exists
        DeviceCatalogItem device = deviceRepo.findById(record.getDeviceItemId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Device not found"));

        // 3️⃣ Set issued details
        record.setIssuedDate(LocalDate.now());
        record.setActive(true);
        record.setReturnedDate(null);

        return issuedRepo.save(record);
    }

    // ✅ Return device
    @Override
    public IssuedDeviceRecord returnDevice(Long recordId) {

        IssuedDeviceRecord record = issuedRepo.findById(recordId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Issued record not found"));

        // Already returned check
        if (!record.getActive()) {
            throw new IllegalStateException("Device already returned");
        }

        record.setActive(false);
        record.setReturnedDate(LocalDate.now());

        return issuedRepo.save(record);
    }

    // ✅ Get issued devices by employee
    @Override
    public List<IssuedDeviceRecord> getIssuedDevicesByEmployee(Long employeeId) {
        return issuedRepo.findByEmployeeId(employeeId);
    }

    // ✅ Get issued record by id
    @Override
    public IssuedDeviceRecord getIssuedDeviceById(Long id) {
        return issuedRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Issued record not found"));
    }
}
