package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.model.EligibilityCheckRecord;
import com.example.demo.service.EligibilityCheckService;

@RestController
@RequestMapping("/api/eligibility")
public class EligibilityCheckController {

    private final EligibilityCheckService service;

    public EligibilityCheckController(EligibilityCheckService service) {
        this.service = service;
    }

    // POST /api/eligibility/validate/{employeeId}/{deviceItemId}
    @PostMapping("/validate/{employeeId}/{deviceItemId}")
    public EligibilityCheckRecord validateEligibility(
            @PathVariable Long employeeId,
            @PathVariable Long deviceItemId) {

        return service.validateEligibility(employeeId, deviceItemId);
    }

    // GET /api/eligibility/employee/{employeeId}
    @GetMapping("/employee/{employeeId}")
    public List<EligibilityCheckRecord> getChecksByEmployee(
            @PathVariable Long employeeId) {

        return service.getChecksByEmployee(employeeId);
    }

    // GET /api/eligibility/{checkId}
    @GetMapping("/{checkId}")
    public EligibilityCheckRecord getCheckById(
            @PathVariable Long checkId) {

        return service.getCheckById(checkId);
    }
}
