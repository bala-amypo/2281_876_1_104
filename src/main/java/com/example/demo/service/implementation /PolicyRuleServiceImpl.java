package com.example.demo.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.model.PolicyRule;
import com.example.demo.repository.PolicyRuleRepository;
import com.example.demo.service.PolicyRuleService;

@Service
public class PolicyRuleServiceImpl implements PolicyRuleService {

    private final PolicyRuleRepository repo;

    public PolicyRuleServiceImpl(PolicyRuleRepository repo) {
        this.repo = repo;
    }

    @Override
    public PolicyRule createRule(PolicyRule rule) {
        if (repo.findByRuleCode(rule.getRuleCode()).isPresent()) {
            throw new BadRequestException("Rule code");
        }
        return repo.save(rule);
    }

    @Override
    public List<PolicyRule> getAllRules() {
        return repo.findAll();
    }

    @Override
    public List<PolicyRule> getActiveRules() {
        return repo.findByActiveTrue();
    }
}
