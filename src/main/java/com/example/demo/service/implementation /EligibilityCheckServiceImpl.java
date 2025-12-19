package com.example.demo.service.implementation;

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

        // 1️⃣ Employee exists & active
        EmployeeProfile employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!employee.getActive()) {
            eligible = false;
            reason = "Employee is inactive";
        }

        // 2️⃣ Device exists & active
        DeviceCatalogItem device = deviceRepo.findById(deviceItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        if (eligible && !device.getActive()) {
            eligible = false;
            reason = "Device is inactive";
        }

        // 3️⃣ No conflicting active issuance
        if (eligible && issuedRepo.findActiveByEmployeeAndDevice(employeeId, deviceItemId) != null) {
            eligible = false;
            reason = "Device already issued to employee";
        }

        // 4️⃣ Max devices per employee (device rule)
        if (eligible) {
            long activeCount = issuedRepo.countActiveDevicesForEmployee(employeeId);

            if (activeCount >= device.getMaxAllowedPerEmployee()) {
                eligible = false;
                reason = "Max allowed devices exceeded";
            }
        }

        // 5️⃣ Policy rules
        if (eligible) {
            List<PolicyRule> rules = policyRepo.findByActiveTrue();

            for (PolicyRule rule : rules) {

                boolean applies =
                        (rule.getAppliesToRole() != null &&
                                rule.getAppliesToRole().equals(employee.getJobRole()))
                        ||
                        (rule.getAppliesToDepartment() != null &&
                                rule.getAppliesToDepartment().equals(employee.getDepartment()));

                if (applies) {
                    long count = issuedRepo.countActiveDevicesForEmployee(employeeId);

                    if (count >= rule.getMaxDevicesAllowed()) {
                        eligible = false;
                        reason = "Policy violation: " + rule.getRuleCode();
                        break;
                    }
                }
            }
        }

        // 6️⃣ Save eligibility check record
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
    public EligibilityCheckRecord getCheckById(Long checkId) {
        return checkRepo.findById(checkId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Eligibility check not found"));
    }

}
