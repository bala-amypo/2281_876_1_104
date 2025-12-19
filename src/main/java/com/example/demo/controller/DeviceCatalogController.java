package com.example.demo.controller;

import java.util.List;

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

    // POST /api/devices
    @PostMapping
    public DeviceCatalogItem createDevice(@RequestBody DeviceCatalogItem item) {
        return service.createItem(item);
    }

    // GET /api/devices
    @GetMapping
    public List<DeviceCatalogItem> getAllDevices() {
        return service.getAllItems();
    }

    // GET /api/devices/{id}
    @GetMapping("/{id}")
    public DeviceCatalogItem getDeviceById(@PathVariable Long id) {
        return service.getItemById(id);
    }

    // PUT /api/devices/{id}/active?active=true
    @PutMapping("/{id}/active")
    public DeviceCatalogItem updateActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return service.updateActiveStatus(id, active);
    }

    // DELETE /api/devices/{id}
    @DeleteMapping("/{id}")
    public void deleteDevice(@PathVariable Long id) {
        service.deleteItem(id);
    }
}
