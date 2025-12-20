package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
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

    // POST /
    @PostMapping
    public ResponseEntity<EmployeeProfile> createEmployee(
            @RequestBody EmployeeProfile employee) {

        return ResponseEntity.ok(service.createEmployee(employee));
    }

    // GET /
    @GetMapping
    public ResponseEntity<List<EmployeeProfile>> getAllEmployees() {
        return ResponseEntity.ok(service.getAllEmployees());
    }

    // GET /{id}
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeProfile> getEmployeeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getEmployeeById(id));
    }

    // PUT /{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<EmployeeProfile> updateStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        return ResponseEntity.ok(service.updateEmployeeStatus(id, active));
    }

    // DELETE /{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        service.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }
}
