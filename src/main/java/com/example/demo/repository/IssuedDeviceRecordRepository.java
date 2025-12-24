package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.IssuedDeviceRecord;

public interface IssuedDeviceRecordRepository
        extends JpaRepository<IssuedDeviceRecord, Long> {

    // ===== Used by IssuedDeviceRecordServiceImpl =====
    List<IssuedDeviceRecord> findByEmployeeId(Long employeeId);

    List<IssuedDeviceRecord> findByEmployeeIdAndStatus(Long employeeId, String status);

    long countByEmployeeIdAndStatus(Long employeeId, String status);

    Optional<IssuedDeviceRecord> findByEmployeeIdAndDeviceItemIdAndStatus(
            Long employeeId, Long deviceItemId, String status);

    // ===== Used DIRECTLY by TESTS =====
    @Query("""
        SELECT r FROM IssuedDeviceRecord r
        WHERE r.employeeId = :empId
          AND r.deviceItemId = :devId
          AND r.status = 'ISSUED'
    """)
    List<IssuedDeviceRecord> findActiveByEmployeeAndDevice(Long empId, Long devId);

    @Query("""
        SELECT COUNT(r) FROM IssuedDeviceRecord r
        WHERE r.employeeId = :empId
          AND r.status = 'ISSUED'
    """)
    Long countActiveDevicesForEmployee(Long empId);
}
