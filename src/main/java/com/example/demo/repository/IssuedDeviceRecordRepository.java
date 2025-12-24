package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.demo.model.IssuedDeviceRecord;

public interface IssuedDeviceRecordRepository
        extends JpaRepository<IssuedDeviceRecord, Long> {

    // Used directly by tests
    @Query("""
        SELECT r FROM IssuedDeviceRecord r
        WHERE r.employeeId = :empId
          AND r.deviceItemId = :devId
          AND r.status = 'ISSUED'
    """)
    List<IssuedDeviceRecord> findActiveByEmployeeAndDevice(Long empId, Long devId);

    // Used directly by tests
    @Query("""
        SELECT COUNT(r) FROM IssuedDeviceRecord r
        WHERE r.employeeId = :empId
          AND r.status = 'ISSUED'
    """)
    Long countActiveDevicesForEmployee(Long empId);
}
