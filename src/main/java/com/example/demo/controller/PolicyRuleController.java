package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.PolicyRule;
import com.example.demo.service.PolicyRuleService;

@RestController
@RequestMapping("/api/policy-rules")
public class PolicyRuleController {

    private final PolicyRuleService policyRuleService;

    public PolicyRuleController(PolicyRuleService policyRuleService) {
        this.policyRuleService = policyRuleService;
    }

    // POST /
    @PostMapping
    public ResponseEntity<PolicyRule> createRule(@RequestBody PolicyRule rule) {
        return new ResponseEntity<>(policyRuleService.createRule(rule), HttpStatus.CREATED);
    }

    // GET /
    @GetMapping
    public List<PolicyRule> getAllRules() {
        return policyRuleService.getAllRules();
    }

    // GET /active
    @GetMapping("/active")
    public List<PolicyRule> getActiveRules() {
        return policyRuleService.getActiveRules();
    }

    // PUT /{id}/active
    @PutMapping("/{id}/active")
    public PolicyRule updateActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        return policyRuleService.updateRuleActive(id, active);
    }

    // DELETE /{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        policyRuleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
