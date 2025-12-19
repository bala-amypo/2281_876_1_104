package com.example.demo.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

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
    public DeviceCatalogItem createItem(DeviceCatalogItem item) {

        // validate maxAllowedPerEmployee
        if (item.getMaxAllowedPerEmployee() <= 0) {
            throw new RuntimeException("maxAllowedPerEmployee");
        }

        // validate unique deviceCode
        repo.findByDeviceCode(item.getDeviceCode())
                .ifPresent(existing -> {
                    throw new RuntimeException(
                            "Device with code already exists: " + item.getDeviceCode());
                });

        return repo.save(item);
    }

    @Override
    public DeviceCatalogItem updateActiveStatus(Long id, boolean active) {
        //to check if id is exists or not
        DeviceCatalogItem item = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Device not found with id: " + id));

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
                        new RuntimeException("Device not found with id: " + id));
    }

    @Override
    public void deleteItem(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Device not found with id: " + id);
        }
        repo.deleteById(id);
    }
}
