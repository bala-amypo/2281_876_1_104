package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.IssuedDeviceRecord;

public interface IssuedDeviceRecordRepository extends JpaRepository<IssuedDeviceRecord, Long> {

    // Find all records for an employee (all statuses)
    List<IssuedDeviceRecord> findByEmployeeId(Long employeeId);

    // Find all active (ISSUED) devices for an employee
    List<IssuedDeviceRecord> findByEmployeeIdAndStatus(Long employeeId, String status);

    // Check if a specific device is already issued and active
    Optional<IssuedDeviceRecord> findByEmployeeIdAndDeviceItemIdAndStatus(
            Long employeeId, Long deviceItemId, String status);

    // Count active (ISSUED) devices for employee
    long countByEmployeeIdAndStatus(Long employeeId, String status);

    // Helper defaults for your test/service code
    default List<IssuedDeviceRecord> findActiveByEmployeeAndDevice(Long employeeId, Long deviceItemId) {
        return findByEmployeeIdAndDeviceItemIdAndStatus(employeeId, deviceItemId, "ISSUED")
                .map(List::of)
                .orElse(List.of());
    }

    default Long countActiveDevicesForEmployee(Long employeeId) {
        return countByEmployeeIdAndStatus(employeeId, "ISSUED");
    }
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

