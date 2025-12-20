package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.EmployeeProfile;
import com.example.demo.repository.EmployeeProfileRepository;
import com.example.demo.service.EmployeeProfileService;

@Service
public class EmployeeProfileServiceImpl implements EmployeeProfileService {

    private final EmployeeProfileRepository repo;

    // ✅ constructor injection (as required)
    public EmployeeProfileServiceImpl(EmployeeProfileRepository repo) {
        this.repo = repo;
    }

    @Override
    public EmployeeProfile createEmployee(EmployeeProfile employee) {

        // EmployeeId uniqueness
        if (repo.findByEmployeeId(employee.getEmployeeId()).isPresent()) {
            throw new RuntimeException("EmployeeId already exists");
        }

        // Email uniqueness
        if (repo.findByEmail(employee.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        return repo.save(employee);
    }

    @Override
    public EmployeeProfile getEmployeeById(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));
    }

    @Override
    public List<EmployeeProfile> getAllEmployees() {
        return repo.findAll();
    }

    @Override
    public EmployeeProfile updateEmployeeStatus(Long id, boolean active) {

        EmployeeProfile employee = getEmployeeById(id);
        employee.setActive(active);

        return repo.save(employee);
    }

    @Override
    public void deleteEmployee(Long id) {
        EmployeeProfile employee = getEmployeeById(id);
        repo.delete(employee);
    }
}
