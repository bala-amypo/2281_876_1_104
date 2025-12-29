package com.example.demo.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import com.example.demo.model.DeviceCatalogItem;
import com.example.demo.service.DeviceCatalogService;

@RestController
@RequestMapping("/api/devices")
public class DeviceCatalogController {

    private final DeviceCatalogService service;

    public DeviceCatalogController(DeviceCatalogService service) {
        this.service = service;
    }

    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public DeviceCatalogItem createDevice(
            @RequestBody DeviceCatalogItem item) {
        return service.createItem(item);
    }

    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','IT_OPERATOR')")
    public List<DeviceCatalogItem> getAllDevices() {
        return service.getAllItems();
    }

    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','IT_OPERATOR')")
    public DeviceCatalogItem getDeviceById(@PathVariable Long id) {
        return service.getItemById(id);
    }

    
    @PutMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public DeviceCatalogItem updateActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return service.updateActiveStatus(id, active);
    }

    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteDevice(@PathVariable Long id) {
        service.deleteItem(id);
    }
}
