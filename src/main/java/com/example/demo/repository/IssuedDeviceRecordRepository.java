package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.IssuedDeviceRecord;

public interface IssuedDeviceRecordRepository
        extends JpaRepository<IssuedDeviceRecord, Long> {

    List<IssuedDeviceRecord> findByEmployeeId(Long employeeId);

    Optional<IssuedDeviceRecord>
    findByEmployeeIdAndDeviceItemIdAndActiveTrue(
            Long employeeId,
            Long deviceItemId
    );

    long countByEmployeeIdAndActiveTrue(Long employeeId);
}


//     // 🔴 CRITICAL: only ACTIVE (ISSUED) record
//     @Query("""
//         SELECT r FROM IssuedDeviceRecord r
//         WHERE r.employeeId = :employeeId
//           AND r.deviceItemId = :deviceItemId
//           AND r.status = 'ISSUED'
//     """)
//     IssuedDeviceRecord findActiveByEmployeeAndDevice(Long employeeId, Long deviceItemId);

//     // 🔴 CRITICAL: count active devices for employee
//     @Query("""
//         SELECT COUNT(r) FROM IssuedDeviceRecord r
//         WHERE r.employeeId = :employeeId
//           AND r.status = 'ISSUED'
//     """)
//     Long countActiveDevicesForEmployee(Long employeeId);

//     List<IssuedDeviceRecord> findByEmployeeId(Long employeeId);

