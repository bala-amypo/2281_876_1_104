package com.example.demo.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import com.example.demo.model.PolicyRule;
import com.example.demo.service.PolicyRuleService;

@RestController
@RequestMapping("/api/policy-rules")
public class PolicyRuleController {

    private final PolicyRuleService service;

    public PolicyRuleController(PolicyRuleService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public PolicyRule createRule(@RequestBody PolicyRule rule) {
        return service.createRule(rule);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<PolicyRule> getAllRules() {
        return service.getAllRules();
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','AUDITOR')")
    public List<PolicyRule> getActiveRules() {
        return service.getActiveRules();
    }

    @PutMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public PolicyRule updateActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return service.updateRuleActive(id, active);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteRule(@PathVariable Long id) {
        service.deleteRule(id);
    }
}
