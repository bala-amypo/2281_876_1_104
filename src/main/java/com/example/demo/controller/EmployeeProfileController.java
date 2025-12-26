package com.example.demo.controller;

import java.util.List;

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

    // POST /api/employees
    @PostMapping
    @PreAuthorize("hasAnyRole('IT_OPERATOR','ADMIN')")
    public EmployeeProfile createEmployee(
            @RequestBody EmployeeProfile employee) {
        return service.createEmployee(employee);
    }

    // GET /api/employees
    @GetMapping
    @PreAuthorize("hasAnyRole('IT_OPERATOR','ADMIN')")
    public List<EmployeeProfile> getAllEmployees() {
        return service.getAllEmployees();
    }

    // GET /api/employees/{id}
    @GetMapping("/{id}")
    public EmployeeProfile getEmployeeById(@PathVariable Long id) {
        return service.getEmployeeById(id);
    }

    // PUT /api/employees/{id}/status?active=true
    @PutMapping("/{id}/status")
    public EmployeeProfile updateStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return service.updateEmployeeStatus(id, active);
    }

    // DELETE /api/employees/{id}
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        service.deleteEmployee(id);
    }
}
