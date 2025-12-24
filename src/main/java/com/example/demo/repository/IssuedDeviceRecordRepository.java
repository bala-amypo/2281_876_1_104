package com.example.demo.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.IssuedDeviceRecord;

@Repository
public interface IssuedDeviceRecordRepository
        extends JpaRepository<IssuedDeviceRecord, Long> {

    boolean existsByEmployeeIdAndDeviceItemIdAndStatus(
            Long employeeId,
            Long deviceItemId,
            String status
    );

    long countByEmployeeIdAndDeviceItemIdAndStatus(
            Long employeeId,
            Long deviceItemId,
            String status
    );

    long countByEmployeeIdAndStatus(
            Long employeeId,
            String status
    );

    Optional<IssuedDeviceRecord> findByEmployeeIdAndDeviceItemIdAndStatus(
            Long employeeId,
            Long deviceItemId,
            String status
    );
    List<IssuedDeviceRecord> findByEmployeeId(Long employeeId);

List<IssuedDeviceRecord> findByEmployeeIdAndStatus(Long employeeId, String status);

}
