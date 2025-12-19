package com.example.demo.service;

import java.util.List;

import com.example.demo.model.DeviceCatalogItem;

public interface DeviceCatalogService {

    DeviceCatalogItem createItem(DeviceCatalogItem item);

    DeviceCatalogItem updateActiveStatus(Long id, boolean active);

    List<DeviceCatalogItem> getAllItems();

    DeviceCatalogItem getItemById(Long id);

    void deleteItem(Long id);
}
