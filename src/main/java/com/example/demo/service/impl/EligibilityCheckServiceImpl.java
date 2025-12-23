package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.DeviceCatalogItem;
import com.example.demo.model.EmployeeProfile;
import com.example.demo.model.EligibilityCheckRecord;
import com.example.demo.model.PolicyRule;
import com.example.demo.repository.DeviceCatalogItemRepository;
import com.example.demo.repository.EmployeeProfileRepository;
import com.example.demo.repository.EligibilityCheckRecordRepository;
import com.example.demo.repository.IssuedDeviceRecordRepository;
import com.example.demo.repository.PolicyRuleRepository;
import com.example.demo.service.EligibilityCheckService;

@Service
public class EligibilityCheckServiceImpl implements EligibilityCheckService {

    private final EmployeeProfileRepository employeeRepo;
    private final DeviceCatalogItemRepository deviceRepo;
    private final IssuedDeviceRecordRepository issuedRepo;
    private final PolicyRuleRepository policyRepo;
    private final EligibilityCheckRecordRepository checkRepo;

    public EligibilityCheckServiceImpl(
            EmployeeProfileRepository employeeRepo,
            DeviceCatalogItemRepository deviceRepo,
            IssuedDeviceRecordRepository issuedRepo,
            PolicyRuleRepository policyRepo,
            EligibilityCheckRecordRepository checkRepo) {

        this.employeeRepo = employeeRepo;
        this.deviceRepo = deviceRepo;
        this.issuedRepo = issuedRepo;
        this.policyRepo = policyRepo;
        this.checkRepo = checkRepo;
    }

    @Override
    public EligibilityCheckRecord validateEligibility(Long employeeId, Long deviceItemId) {

    EmployeeProfile employee = employeeRepo.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

    if (!Boolean.TRUE.equals(employee.getActive())) {
        throw new BadRequestException("not active");
    }

    DeviceCatalogItem device = deviceRepo.findById(deviceItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

    if (!Boolean.TRUE.equals(device.getActive())) {
        throw new BadRequestException("inactive");
    }

    // already issued same device
    if (!issuedRepo.findByEmployeeIdAndDeviceItemIdAndStatus(
            employeeId, deviceItemId, "ISSUED").isEmpty()) {
        throw new BadRequestException("already assigned");
    }

    // device-level max
    Integer maxAllowed = device.getMaxAllowedPerEmployee();
    if (maxAllowed != null) {
        long activeCount = issuedRepo.countByEmployeeIdAndStatus(employeeId, "ISSUED");
        if (activeCount >= maxAllowed) {
            return saveResult(employeeId, deviceItemId, false, "device limit");
        }
    }

    // policy rules
    List<PolicyRule> rules = policyRuleRepo.findActiveRules();
    for (PolicyRule rule : rules) {

        boolean applies =
                (rule.getAppliesToDepartment() == null ||
                        rule.getAppliesToDepartment().equals(employee.getDepartment())) &&
                (rule.getAppliesToRole() == null ||
                        rule.getAppliesToRole().equals(employee.getJobRole()));

        if (applies && rule.getMaxDevicesAllowed() != null) {
            long issuedCount = issuedRepo.countByEmployeeIdAndStatus(employeeId, "ISSUED");
            if (issuedCount >= rule.getMaxDevicesAllowed()) {
                return saveResult(employeeId, deviceItemId, false, "Policy violation");
            }
        }
    }

    return saveResult(employeeId, deviceItemId, true, "Eligible");
    }

    private EligibilityCheckRecord saveResult(Long empId, Long devId, boolean eligible, String reason) {
    EligibilityCheckRecord r = new EligibilityCheckRecord();
    r.setEmployeeId(empId);
    r.setDeviceItemId(devId);
    r.setEligible(eligible);
    r.setReason(reason);
    return eligibilityRepo.save(r);
    }


    @Override
    public List<EligibilityCheckRecord> getChecksByEmployee(Long employeeId) {
        return checkRepo.findByEmployeeId(employeeId);
    }

    @Override
    public EligibilityCheckRecord getById(Long id) {
        return checkRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Eligibility check not found"));
    }
}
