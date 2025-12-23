
package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.DeviceDto;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.DeviceCatalogItem;
import com.example.demo.repository.DeviceCatalogItemRepository;
import com.example.demo.service.DeviceCatalogService;

@Service
public class DeviceCatalogServiceImpl implements DeviceCatalogService {

    private final DeviceCatalogItemRepository repo;

    public DeviceCatalogServiceImpl(DeviceCatalogItemRepository repo) {
        this.repo = repo;
    }

    @Override
    public DeviceCatalogItem createItem(DeviceDto dto) {

        // ✅ Validate maxAllowedPerEmployee
        if (dto.getMaxAllowedPerEmployee() == null ||
            dto.getMaxAllowedPerEmployee() <= 0) {
            throw new BadRequestException("maxAllowedPerEmployee");
        }

        // ✅ Validate duplicate device code
        if (repo.findByDeviceCode(dto.getDeviceCode()).isPresent()) {
            throw new BadRequestException("Device code already exists");
        }

        DeviceCatalogItem item = new DeviceCatalogItem();
        item.setDeviceCode(dto.getDeviceCode());
        item.setDeviceType(dto.getDeviceType());
        item.setModel(dto.getModel());
        item.setMaxAllowedPerEmployee(dto.getMaxAllowedPerEmployee());
        item.setActive(dto.getActive() != null ? dto.getActive() : true);

        return repo.save(item);
    }

    @Override
    public DeviceCatalogItem updateActiveStatus(Long id, boolean active) {
        DeviceCatalogItem item = repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Device not found"));

        item.setActive(active);
        return repo.save(item);
    }

    @Override
    public List<DeviceCatalogItem> getAllItems() {
        return repo.findAll();
    }

    @Override
    public DeviceCatalogItem getItemById(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Device not found"));
    }

    @Override
    public void deleteItem(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Device not found");
        }
        repo.deleteById(id);
    }
}
