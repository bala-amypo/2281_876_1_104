package com.example.demo.service;

import java.util.List;
import com.example.demo.model.PolicyRule;

public interface PolicyRuleService {

    PolicyRule createRule(PolicyRule rule);

    List<PolicyRule> getAllRules();

    List<PolicyRule> getActiveRules();
}
