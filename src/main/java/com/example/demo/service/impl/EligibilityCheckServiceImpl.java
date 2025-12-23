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
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        DeviceCatalogItem device = deviceRepo.findById(deviceItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Device not found"));

        EligibilityCheckRecord record = new EligibilityCheckRecord();
        record.setEmployeeId(employeeId);
        record.setDeviceItemId(deviceItemId);

        // ❌ Employee not active
        if (!employee.getActive()) {
            record.setIsEligible(false);
            record.setReason("not active");
            checkRepo.save(record);
            throw new BadRequestException("not active");
        }

        // ❌ Device inactive
        if (!device.getActive()) {
            record.setIsEligible(false);
            record.setReason("inactive");
            checkRepo.save(record);
            throw new BadRequestException("inactive");
        }

        // ❌ Active issuance exists
        long activeIssued =
                issuedRepo.countByEmployeeIdAndStatus(employeeId, "ISSUED");

        if (activeIssued > 0) {
            record.setIsEligible(false);
            record.setReason("active issuance");
            checkRepo.save(record);
            throw new BadRequestException("active issuance");
        }

        // ❌ Max devices reached
        if (activeIssued >= device.getMaxAllowedPerEmployee()) {
            record.setIsEligible(false);
            record.setReason("Maximum allowed devices");
            checkRepo.save(record);
            throw new BadRequestException("Maximum allowed devices");
        }

        // ❌ Policy rule violation
        List<PolicyRule> rules = policyRepo.findByActiveTrue();
        for (PolicyRule rule : rules) {

            boolean applies =
                    (rule.getAppliesToRole() != null &&
                     rule.getAppliesToRole().equals(employee.getJobRole()))
                    ||
                    (rule.getAppliesToDepartment() != null &&
                     rule.getAppliesToDepartment().equals(employee.getDepartment()));

            if (applies && activeIssued >= rule.getMaxDevicesAllowed()) {
                record.setIsEligible(false);
                record.setReason("Policy violation");
                checkRepo.save(record);
                throw new BadRequestException("Policy violation");
            }
        }

        // ✅ Eligible
        record.setIsEligible(true);
        record.setReason("Eligible");
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
