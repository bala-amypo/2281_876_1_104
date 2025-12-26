package com.example.demo.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

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

    @PostMapping("/validate/{employeeId}/{deviceItemId}")
    @PreAuthorize("hasAnyRole('ADMIN','IT_OPERATOR')")
    public EligibilityCheckRecord validate(
            @PathVariable Long employeeId,
            @PathVariable Long deviceItemId) {

        return service.validateEligibility(employeeId, deviceItemId);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('AUDITOR','ADMIN')")
    public List<EligibilityCheckRecord> byEmployee(@PathVariable Long employeeId) {
        return service.getChecksByEmployee(employeeId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUDITOR','ADMIN')")
    public EligibilityCheckRecord getById(@PathVariable Long id) {
        return service.getById(id);
    }
}
