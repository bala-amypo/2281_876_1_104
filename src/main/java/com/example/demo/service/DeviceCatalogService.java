package com.example.demo.service;

import java.util.List;
import com.example.demo.model.DeviceCatalogItem;

public interface DeviceCatalogService {

    DeviceCatalogItem createItem(DeviceCatalogItem item);

    void updateActiveStatus(Long id, boolean active);

    List<DeviceCatalogItem> getAllItems();
}
