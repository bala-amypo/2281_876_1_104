package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.IssuedDeviceRecord;
import com.example.demo.service.IssuedDeviceRecordService;

@RestController
@RequestMapping("/api/issued-devices")
public class IssuedDeviceRecordController {

    @Autowired
    private IssuedDeviceRecordService service;

    @PostMapping("/")
    public IssuedDeviceRecord issueDevice(@RequestBody IssuedDeviceRecord record) {
        return service.issueDevice(record);
    }

    @PutMapping("/{id}/return")
    public String returnDevice(@PathVariable Long id) {
        service.returnDevice(id);
        return "Device returned successfully";
    }

    @GetMapping("/employee/{employeeId}")
    public List<IssuedDeviceRecord> getByEmployee(@PathVariable Long employeeId) {
        return service.getIssuedDevicesByEmployee(employeeId);
    }
}
