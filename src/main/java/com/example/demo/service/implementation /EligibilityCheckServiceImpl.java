package com.example.demo.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
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

        String reason = "Eligible";
        boolean eligible = true;

        // 1️⃣ Check employee exists & active
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!employee.isActive()) {
            eligible = false;
            reason = "Employee is inactive";
        }

        // 2️⃣ Check device exists & active
        DeviceCatalogItem device = deviceRepo.findById(deviceItemId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        if (eligible && !device.isActive()) {
            eligible = false;
            reason = "Device is inactive";
        }

        // 3️⃣ Check no active issuance exists for employee-device
        if (eligible && issuedRepo
                .existsByEmployeeIdAndDeviceItemIdAndActiveTrue(employeeId, deviceItemId)) {

            eligible = false;
            reason = "Device already issued to employee";
        }

        // 4️⃣ Count active devices for employee
        if (eligible) {
            int activeDeviceCount =
                    issuedRepo.countByEmployeeIdAndActiveTrue(employeeId);

            if (activeDeviceCount >= device.getMaxAllowedPerEmployee()) {
                eligible = false;
                reason = "Max allowed devices exceeded";
            }
        }

        // 5️⃣ Check policy rules
        if (eligible) {
            List<PolicyRule> activePolicies = policyRepo.findByActiveTrue();

            for (PolicyRule policy : activePolicies) {

                boolean applies =
                        (policy.getAppliesToRole() != null &&
                         policy.getAppliesToRole().equals(employee.getRole()))
                        ||
                        (policy.getAppliesToDepartment() != null &&
                         policy.getAppliesToDepartment().equals(employee.getDepartment()));

                if (applies) {
                    int currentCount =
                            issuedRepo.countByEmployeeIdAndActiveTrue(employeeId);

                    if (currentCount >= policy.getMaxDevicesAllowed()) {
                        eligible = false;
                        reason = "Policy violation: " + policy.getName();
                        break;
                    }
                }
            }
        }

        // 6️⃣ Create eligibility check record
        EligibilityCheckRecord record = new EligibilityCheckRecord();
        record.setEmployeeId(employeeId);
        record.setDeviceItemId(deviceItemId);
        record.setEligible(eligible);
        record.setReason(reason);

        return checkRepo.save(record);
    }

    @Override
    public List<EligibilityCheckRecord> getChecksByEmployee(Long employeeId) {
        return checkRepo.findByEmployeeId(employeeId);
    }

    @Override
    public EligibilityCheckRecord getCheckById(Long checkId) {
        return checkRepo.findById(checkId)
                .orElseThrow(() ->
                        new RuntimeException("Eligibility check not found"));
    }
}
