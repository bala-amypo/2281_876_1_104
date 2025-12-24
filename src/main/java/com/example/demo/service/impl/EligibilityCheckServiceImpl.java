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
    
    private final EligibilityCheckRecordRepository eligibilityRepo;


    public EligibilityCheckServiceImpl(
            EmployeeProfileRepository employeeRepo,
            DeviceCatalogItemRepository deviceRepo,
            IssuedDeviceRecordRepository issuedRepo,
            PolicyRuleRepository policyRepo,
            EligibilityCheckRecordRepository eligibilityRepo) {

        this.employeeRepo = employeeRepo;
        this.deviceRepo = deviceRepo;
        this.issuedRepo = issuedRepo;
        this.policyRepo = policyRepo;
        this.eligibilityRepo = eligibilityRepo;
    }
    @Override
    public EligibilityCheckRecord validateEligibility(Long employeeId, Long deviceItemId) {

    EligibilityCheckRecord rec = new EligibilityCheckRecord();
    rec.setEmployeeId(employeeId);
    rec.setDeviceItemId(deviceItemId);

    EmployeeProfile emp = employeeRepo.findById(employeeId).orElse(null);
    DeviceCatalogItem dev = deviceRepo.findById(deviceItemId).orElse(null);

    if (emp == null || dev == null) {
        rec.setIsEligible(false);
        rec.setReason("Employee or Device not found");
        return eligibilityRepo.save(rec);
    }

    if (!emp.getActive()) {
        rec.setIsEligible(false);
        rec.setReason("Employee not active");
        return eligibilityRepo.save(rec);
    }

    if (!dev.getActive()) {
        rec.setIsEligible(false);
        rec.setReason("Device inactive");
        return eligibilityRepo.save(rec);
    }

    // 🔑 ACTIVE ASSIGNMENT CHECK (TEST 57)
    if (!issuedRepo.findActiveByEmployeeAndDevice(employeeId, deviceItemId).isEmpty()) {
        rec.setIsEligible(false);
        rec.setReason("Active issuance exists");
        return eligibilityRepo.save(rec);
    }

    // 🔑 DEVICE MAX CHECK (TEST 58)
    long activeCount = issuedRepo.countActiveDevicesForEmployee(employeeId);
    if (activeCount >= dev.getMaxAllowedPerEmployee()) {
        rec.setIsEligible(false);
        rec.setReason("Maximum allowed devices reached");
        return eligibilityRepo.save(rec);
    }

    // 🔑 POLICY CHECKS (TEST 59, 63)
    for (PolicyRule rule : policyRepo.findByActiveTrue()) {

        boolean deptOk = rule.getAppliesToDepartment() == null
                || rule.getAppliesToDepartment().equals(emp.getDepartment());

        boolean roleOk = rule.getAppliesToRole() == null
                || rule.getAppliesToRole().equals(emp.getJobRole());

        if (deptOk && roleOk) {
            if (activeCount >= rule.getMaxDevicesAllowed()) {
                rec.setIsEligible(false);
                rec.setReason("Policy violation");
                return eligibilityRepo.save(rec);
            }
        }
    }

    rec.setIsEligible(true);
    rec.setReason("Eligible");
    return eligibilityRepo.save(rec);
    }


    @Override
    public List<EligibilityCheckRecord> getChecksByEmployee(Long employeeId) {
        return eligibilityRepo.findByEmployeeId(employeeId);
    }

    @Override
    public EligibilityCheckRecord getById(Long id) {
        return eligibilityRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Eligibility check not found"));
    }
}
