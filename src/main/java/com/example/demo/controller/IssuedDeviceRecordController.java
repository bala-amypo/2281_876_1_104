package com.example.demo.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.IssuedDeviceRecord;
import com.example.demo.service.IssuedDeviceRecordService;

@RestController
@RequestMapping("/api/issued-devices")
public class IssuedDeviceRecordController {

    private final IssuedDeviceRecordService service;

    public IssuedDeviceRecordController(IssuedDeviceRecordService service) {
        this.service = service;
    }

    
    @PostMapping
    @PreAuthorize("hasAnyRole('IT_OPERATOR','ADMIN')")
    public ResponseEntity<IssuedDeviceRecord> issueDevice(
            @RequestBody IssuedDeviceRecord record) {
        return ResponseEntity.ok(service.issueDevice(record));
    }

    
    @PutMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('IT_OPERATOR','ADMIN')")
    public ResponseEntity<IssuedDeviceRecord> returnDevice(@PathVariable Long id) {
        return ResponseEntity.ok(service.returnDevice(id));
    }

    
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('IT_OPERATOR','ADMIN')")
    public ResponseEntity<List<IssuedDeviceRecord>> getByEmployee(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(service.getByEmployeeId(employeeId));
    }

   
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('IT_OPERATOR','ADMIN')")
    public ResponseEntity<IssuedDeviceRecord> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
}
