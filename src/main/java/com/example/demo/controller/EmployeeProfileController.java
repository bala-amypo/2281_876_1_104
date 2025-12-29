package com.example.demo.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import com.example.demo.model.EmployeeProfile;
import com.example.demo.service.EmployeeProfileService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeProfileController {

    private final EmployeeProfileService service;

    public EmployeeProfileController(EmployeeProfileService service) {
        this.service = service;
    }

    
    @PostMapping
    @PreAuthorize("hasAnyRole('IT_OPERATOR','ADMIN')")
    public EmployeeProfile createEmployee(
            @RequestBody EmployeeProfile employee) {
        return service.createEmployee(employee);
    }

    
    @GetMapping
    @PreAuthorize("hasAnyRole('IT_OPERATOR','ADMIN')")
    public List<EmployeeProfile> getAllEmployees() {
        return service.getAllEmployees();
    }

    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('IT_OPERATOR','ADMIN')")
    public EmployeeProfile getEmployeeById(@PathVariable Long id) {
        return service.getEmployeeById(id);
    }

    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public EmployeeProfile updateStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return service.updateEmployeeStatus(id, active);
    }

    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteEmployee(@PathVariable Long id) {
        service.deleteEmployee(id);
    }
}
