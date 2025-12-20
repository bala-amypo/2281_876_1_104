package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.IssuedDeviceRecord;

public interface IssuedDeviceRecordRepository extends JpaRepository<IssuedDeviceRecord, Long> {

    List<IssuedDeviceRecord> findByEmployeeId(Long employeeId);

    IssuedDeviceRecord findActiveByEmployeeAndDevice(Long employeeId, Long deviceItemId);

    long countActiveDevicesForEmployee(Long employeeId); 
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

