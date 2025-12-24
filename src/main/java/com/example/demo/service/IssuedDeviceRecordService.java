package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.IssuedDeviceRecord;

@Repository
public interface IssuedDeviceRecordRepository
        extends JpaRepository<IssuedDeviceRecord, Long> {

    // ✔ Used for "Active assignment already exists" test
    boolean existsByEmployeeIdAndDeviceItemIdAndStatus(
            Long employeeId,
            Long deviceItemId,
            String status
    );

    // ✔ Used for device-level max allowed test
    long countByEmployeeIdAndDeviceItemIdAndStatus(
            Long employeeId,
            Long deviceItemId,
            String status
    );

    // ✔ Used for policy-rule checks
    long countByEmployeeIdAndStatus(
            Long employeeId,
            String status
    );

    // (Optional but usually required by other tests)
    Optional<IssuedDeviceRecord> findByEmployeeIdAndDeviceItemIdAndStatus(
            Long employeeId,
            Long deviceItemId,
            String status
    );
}


