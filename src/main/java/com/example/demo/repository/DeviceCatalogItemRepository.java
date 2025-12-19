package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.DeviceCatalogItem;

public interface DeviceCatalogItemRepository
        extends JpaRepository<DeviceCatalogItem, Long> {

    Optional<DeviceCatalogItem> findByDeviceCode(String deviceCode);
}
