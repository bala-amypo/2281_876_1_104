
package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.DeviceDto;
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
    public DeviceCatalogItem createDevice(@RequestBody DeviceDto dto) {
        return service.createItem(dto);
    }

    @GetMapping
    public List<DeviceCatalogItem> getAllDevices() {
        return service.getAllItems();
    }

    @GetMapping("/{id}")
    public DeviceCatalogItem getDeviceById(@PathVariable Long id) {
        return service.getItemById(id);
    }

    @PutMapping("/{id}/active")
    public DeviceCatalogItem updateActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return service.updateActiveStatus(id, active);
    }

    @DeleteMapping("/{id}")
    public void deleteDevice(@PathVariable Long id) {
        service.deleteItem(id);
    }
}

