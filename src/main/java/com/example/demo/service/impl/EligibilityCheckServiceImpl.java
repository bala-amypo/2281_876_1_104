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

        // 1️⃣ Employee existence check ONLY
        EmployeeProfile employee = employeeRepo.findById(employeeId).orElse(null);
        if (employee == null) {
            eligible = false;
            reason = "Employee not found";
        }

        // 2️⃣ Device checks
        DeviceCatalogItem device = deviceRepo.findById(deviceItemId).orElse(null);
        if (eligible && device == null) {
            eligible = false;
            reason = "Device not found";
        } else if (eligible && !Boolean.TRUE.equals(device.getActive())) {
            eligible = false;
            reason = "Device is inactive";
        }

        // 3️⃣ Any active issued device blocks eligibility
        if (eligible) {
            long activeIssued = issuedRepo.countByEmployeeIdAndStatus(employeeId, "ISSUED");
            if (activeIssued > 0) {
                eligible = false;
                reason = "Active assignment already exists";
            }
        }

        // 4️⃣ Device-level max per employee (NULL SAFE)
        if (eligible) {
            Integer maxAllowed = device.getMaxAllowedPerEmployee();
            long issuedCount = issuedRepo.countByEmployeeIdAndStatus(employeeId, "ISSUED");

            if (maxAllowed != null && issuedCount >= maxAllowed) {
                eligible = false;
                reason = "Max devices reached for device";
            }
        }

        // 5️⃣ Policy rules (NULL SAFE)
        if (eligible) {
            List<PolicyRule> rules = policyRepo.findByActiveTrue();
            long activeCount = issuedRepo.countByEmployeeIdAndStatus(employeeId, "ISSUED");

            for (PolicyRule rule : rules) {

                boolean applies =
                        (rule.getAppliesToRole() == null && rule.getAppliesToDepartment() == null)
                        ||
                        (rule.getAppliesToRole() != null &&
                                rule.getAppliesToRole().equals(employee.getJobRole()))
                        ||
                        (rule.getAppliesToDepartment() != null &&
                                rule.getAppliesToDepartment().equals(employee.getDepartment()));

                Integer maxRuleAllowed = rule.getMaxDevicesAllowed();

                if (applies && maxRuleAllowed != null && activeCount >= maxRuleAllowed) {
                    eligible = false;
                    reason = "Policy violation: " + rule.getRuleCode();
                    break;
                }
            }
        }

        // 6️⃣ Always save eligibility check record
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
