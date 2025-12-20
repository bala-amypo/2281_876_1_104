public interface IssuedDeviceRecordRepository
        extends JpaRepository<IssuedDeviceRecord, Long> {

    List<IssuedDeviceRecord> findByEmployeeId(Long employeeId);

    IssuedDeviceRecord findByEmployeeIdAndDeviceItemIdAndActiveTrue(
            Long employeeId,
            Long deviceItemId
    );

    long countByEmployeeIdAndActiveTrue(Long employeeId);
}
