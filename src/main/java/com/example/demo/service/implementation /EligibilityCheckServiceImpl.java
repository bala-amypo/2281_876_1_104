@Service
public class EligibilityCheckServiceImpl implements EligibilityCheckService {

    private final EmployeeProfileRepository employeeRepo;
    private final DeviceCatalogItemRepository deviceRepo;
    private final IssuedDeviceRecordRepository issuedRepo;
    private final PolicyRuleRepository policyRepo;
    private final EligibilityCheckRecordRepository checkRepo;

    public EligibilityCheckServiceImpl(
            EmployeeProfileRepository employeeRepo,
            DeviceCatalogItemRepository deviceRepo,
            IssuedDeviceRecordRepository issuedRepo,
            PolicyRuleRepository policyRepo,
            EligibilityCheckRecordRepository checkRepo) {

        this.employeeRepo = employeeRepo;
        this.deviceRepo = deviceRepo;
        this.issuedRepo = issuedRepo;
        this.policyRepo = policyRepo;
        this.checkRepo = checkRepo;
    }

    @Override
    public EligibilityCheckRecord validateEligibility(Long employeeId, Long deviceItemId) {

        boolean eligible = true;
        String reason = "Eligible";

        EmployeeProfile employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!employee.getActive()) {
            eligible = false;
            reason = "Employee is inactive";
        }

        DeviceCatalogItem device = deviceRepo.findById(deviceItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        if (eligible && !device.getActive()) {
            eligible = false;
            reason = "Device is inactive";
        }

        if (eligible && issuedRepo.findActiveByEmployeeAndDevice(employeeId, deviceItemId) != null) {
            eligible = false;
            reason = "Device already issued to employee";
        }

        if (eligible) {
            long count = issuedRepo.countActiveDevicesForEmployee(employeeId);
            if (count >= device.getMaxAllowedPerEmployee()) {
                eligible = false;
                reason = "Max allowed devices exceeded";
            }
        }

        if (eligible) {
            for (PolicyRule rule : policyRepo.findByActiveTrue()) {

                boolean applies =
                        (rule.getAppliesToRole() != null &&
                         rule.getAppliesToRole().equals(employee.getJobRole()))
                        ||
                        (rule.getAppliesToDepartment() != null &&
                         rule.getAppliesToDepartment().equals(employee.getDepartment()));

                if (applies) {
                    long count = issuedRepo.countActiveDevicesForEmployee(employeeId);
                    if (count >= rule.getMaxDevicesAllowed()) {
                        eligible = false;
                        reason = "Policy violation: " + rule.getRuleCode();
                        break;
                    }
                }
            }
        }

        EligibilityCheckRecord record = new EligibilityCheckRecord();
        record.setEmployeeId(employeeId);
        record.setDeviceItemId(deviceItemId);
        record.setIsEligible(eligible);
        record.setReason(reason);

        return checkRepo.save(record);
    }

    @Override
    public List<EligibilityCheckRecord> getChecksByEmployee(Long employeeId) {
        return checkRepo.findByEmployeeId(employeeId);
    }

    @Override
    public EligibilityCheckRecord getCheckById(Long id) {
        return checkRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Eligibility check not found"));
    }
}
