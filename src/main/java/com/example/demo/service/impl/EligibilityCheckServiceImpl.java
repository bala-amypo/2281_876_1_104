package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

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

        boolean eligible = true;
        String reason = "Eligible";

        // 1️⃣ Fetch employee (do NOT invalidate if inactive)
        EmployeeProfile employee = employeeRepo.findById(employeeId).orElse(null);
        if (employee == null) {
            eligible = false;
            reason = "Employee not found";
        }

        // 2️⃣ Fetch device (do NOT invalidate if inactive)
        DeviceCatalogItem device = deviceRepo.findById(deviceItemId).orElse(null);
        if (eligible && device == null) {
            eligible = false;
            reason = "Device not found";
        }

        // 3️⃣ Active assignment exists (same employee + same device)
        if (eligible && issuedRepo
                .existsByEmployeeIdAndDeviceItemIdAndStatus(
                        employeeId, deviceItemId, "ISSUED")) {

            eligible = false;
            reason = "Active assignment already exists";
        }

        // 4️⃣ Device-level max allowed (COUNT SAME DEVICE ONLY)
        if (eligible && device.getMaxAllowedPerEmployee() != null) {

            long issuedCountForDevice =
                    issuedRepo.countByEmployeeIdAndDeviceItemIdAndStatus(
                            employeeId, deviceItemId, "ISSUED");

            if (issuedCountForDevice >= device.getMaxAllowedPerEmployee()) {
                eligible = false;
                reason = "Max devices reached for device";
            }
        }

        // 5️⃣ Policy rules
        if (eligible) {

            long activeIssuedCount =
                    issuedRepo.countByEmployeeIdAndStatus(employeeId, "ISSUED");

            List<PolicyRule> rules = policyRepo.findByActiveTrue();

            for (PolicyRule rule : rules) {

                boolean applies =
                        (rule.getAppliesToRole() == null && rule.getAppliesToDepartment() == null)
                        ||
                        (rule.getAppliesToRole() != null &&
                                rule.getAppliesToRole().equals(employee.getJobRole()))
                        ||
                        (rule.getAppliesToDepartment() != null &&
                                rule.getAppliesToDepartment().equals(employee.getDepartment()));

                if (applies &&
                        rule.getMaxDevicesAllowed() != null &&
                        activeIssuedCount >= rule.getMaxDevicesAllowed()) {

                    eligible = false;
                    reason = "Policy violation: " + rule.getRuleCode();
                    break;
                }
            }
        }

        // 6️⃣ Always save eligibility check
        EligibilityCheckRecord record = new EligibilityCheckRecord();
        record.setEmployeeId(employeeId);
        record.setDeviceItemId(deviceItemId);
        record.setIsEligible(eligible);
        record.setReason(reason);

        return checkRepo.save(record);
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
