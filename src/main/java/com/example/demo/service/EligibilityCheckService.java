package com.example.demo.service;

import java.util.List;
import com.example.demo.model.EligibilityCheckRecord;

public interface EligibilityCheckService {

    // Validate if an employee is eligible to get a device
    EligibilityCheckRecord validateEligibility(Long employeeId, Long deviceItemId);

    // Get all eligibility check records for an employee
    List<EligibilityCheckRecord> getChecksByEmployee(Long employeeId);

    // Get a single eligibility check record by its ID
    EligibilityCheckRecord getById(Long id);
}
