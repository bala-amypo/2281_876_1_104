package com.example.demo.service.implementation;

import java.util.*;

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
        return repo.save(item);
    }

    @Override
    public void updateActiveStatus(Long id, boolean active) {
    Optional<DeviceCatalogItem> optionalItem = repo.findById(id);

    if (optionalItem.isPresent()) {
        DeviceCatalogItem item = optionalItem.get();
        item.setActive(active);
        repo.save(item);
    } else {
        throw new RuntimeException("DeviceCatalogItem not found with id: " + id);
    }
    }
    @Override
    public List<DeviceCatalogItem> getAllItems() {
        return repo.findAll();
    }
}





