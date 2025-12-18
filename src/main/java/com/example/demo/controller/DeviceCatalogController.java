package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.DeviceCatalogItem;
import com.example.demo.service.DeviceCatalogService;

@RestController
@RequestMapping("/api/devices")
public class DeviceCatalogController {

    @Autowired
    private DeviceCatalogService service;

    @PostMapping("/")
    public DeviceCatalogItem createItem(@RequestBody DeviceCatalogItem item) {
        return service.createItem(item);
    }

    @PutMapping("/{id}/active")
    public String updateActiveStatus(@PathVariable Long id, @RequestParam boolean active) {
        service.updateActiveStatus(id, active);
        return "Device active status updated";
    }

    @GetMapping("/")
    public List<DeviceCatalogItem> getAllItems() {
        return service.getAllItems();
    }
}
