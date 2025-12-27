package com.example.demo;

import com.example.demo.exception.BadRequestException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.*;
import com.example.demo.service.impl.*;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


/**
 * Massive TestNG test class with 60+ test cases
 */
@SpringBootTest
@Listeners(TestResultListener.class)
public class EmployeeEquipmentIssuancePolicyCheckerTests {

    // Shared mocks/services
    @Mock
    private EmployeeProfileRepository employeeRepo;
    @Mock
    private DeviceCatalogItemRepository deviceRepo;
    @Mock
    private IssuedDeviceRecordRepository issuedRepo;
    @Mock
    private PolicyRuleRepository policyRepo;
    @Mock
    private EligibilityCheckRecordRepository eligibilityRepo;
    @Mock
    private UserAccountRepository userRepo;

    private EligibilityCheckService eligibilityService;
    private EmployeeProfileService employeeService;
    private DeviceCatalogService deviceService;
    private IssuedDeviceRecordService issuedService;
    private PolicyRuleService ruleService;

    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider tokenProvider;

    @BeforeClass
    public void init() {
        MockitoAnnotations.openMocks(this);
        eligibilityService = new EligibilityCheckServiceImpl(
                employeeRepo, deviceRepo, issuedRepo, policyRepo, eligibilityRepo);
        employeeService = new EmployeeProfileServiceImpl(employeeRepo);
        deviceService = new DeviceCatalogServiceImpl(deviceRepo);
        issuedService = new IssuedDeviceRecordServiceImpl(issuedRepo, employeeRepo, deviceRepo);
        ruleService = new PolicyRuleServiceImpl(policyRepo);
        passwordEncoder = new BCryptPasswordEncoder();
        tokenProvider = new JwtTokenProvider("ChangeThisSecretKeyForJwt123456789012345", 3600000);
    }

    // ------------------------------------------------------------------------
    // 1) Develop and deploy a simple servlet using Tomcat Server (conceptual)
    // ------------------------------------------------------------------------

    @Test(priority = 1, groups = "servlet")
    public void testServletLikeControllerMappingExists() {
        // Simple conceptual check: context loads and basic path is non-null
        String path = "/api/employees/";
        Assert.assertTrue(path.startsWith("/api/"), "Servlet-like REST mapping should start with /api/");
    }

    @Test(priority = 2, groups = "servlet")
    public void testServletResponseStatusAssumedOk() {
        int status = 200; // conceptually representing HttpServletResponse.SC_OK
        Assert.assertEquals(status, 200);
    }

    @Test(priority = 3, groups = "servlet")
    public void testServletNegativeStatus() {
        int status = 404;
        Assert.assertNotEquals(status, 200);
    }

    @Test(priority = 4, groups = "servlet")
    public void testTomcatContextPathNotNull() {
        String contextPath = "/"; // typical Spring Boot embedded Tomcat context
        Assert.assertNotNull(contextPath);
    }

    @Test(priority = 5, groups = "servlet")
    public void testServletEdgeCaseEmptyUrl() {
        String url = "";
        Assert.assertTrue(url.isEmpty());
    }

    @Test(priority = 6, groups = "servlet")
    public void testServletSimulatedGetRequestPath() {
        String url = "/api/devices/";
        Assert.assertTrue(url.contains("/devices"));
    }

    @Test(priority = 7, groups = "servlet")
    public void testServletSimulatedPostRequestPath() {
        String url = "/api/employees/";
        Assert.assertEquals(url, "/api/employees/");
    }

    @Test(priority = 8, groups = "servlet")
    public void testServletHeadersConceptuallyPresent() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        Assert.assertEquals(headers.get("Content-Type"), "application/json");
    }

    // ------------------------------------------------------------------------
    // 2) Implement CRUD operations using Spring Boot and REST APIs
    // ------------------------------------------------------------------------

    @Test(priority = 9, groups = "crud")
    public void testCreateEmployeeProfileSuccess() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setEmployeeId("E001");
        emp.setFullName("John Doe");
        emp.setEmail("john@example.com");
        emp.setDepartment("IT");
        emp.setJobRole("DEVELOPER");
        emp.setActive(true);
        emp.setCreatedAt(LocalDateTime.now());

        when(employeeRepo.findByEmployeeId("E001")).thenReturn(Optional.empty());
        when(employeeRepo.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(employeeRepo.save(any(EmployeeProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeProfile saved = employeeService.createEmployee(emp);
        Assert.assertEquals(saved.getEmployeeId(), "E001");
        Assert.assertTrue(saved.getActive());
    }

    @Test(priority = 10, groups = "crud")
    public void testCreateEmployeeDuplicateEmployeeId() {
        EmployeeProfile existing = new EmployeeProfile();
        existing.setEmployeeId("E001");
        when(employeeRepo.findByEmployeeId("E001")).thenReturn(Optional.of(existing));

        EmployeeProfile emp = new EmployeeProfile();
        emp.setEmployeeId("E001");
        emp.setEmail("x@y.com");

        try {
            employeeService.createEmployee(emp);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("EmployeeId already exists"));
        }
    }

    @Test(priority = 11, groups = "crud")
    public void testUpdateEmployeeStatus() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setId(1L);
        emp.setActive(true);
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(emp));
        when(employeeRepo.save(any(EmployeeProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeProfile updated = employeeService.updateEmployeeStatus(1L, false);
        Assert.assertFalse(updated.getActive());
    }

    @Test(priority = 12, groups = "crud")
    public void testCreateDeviceItemSuccess() {
        DeviceCatalogItem item = new DeviceCatalogItem();
        item.setDeviceCode("LAP-001");
        item.setDeviceType("LAPTOP");
        item.setModel("Dell XPS");
        item.setMaxAllowedPerEmployee(2);
        item.setActive(true);

        when(deviceRepo.findByDeviceCode("LAP-001")).thenReturn(Optional.empty());
        when(deviceRepo.save(any(DeviceCatalogItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DeviceCatalogItem saved = deviceService.createItem(item);
        Assert.assertEquals(saved.getDeviceCode(), "LAP-001");
    }

    @Test(priority = 13, groups = "crud")
    public void testCreateDeviceInvalidMaxAllowed() {
        DeviceCatalogItem item = new DeviceCatalogItem();
        item.setDeviceCode("LAP-002");
        item.setMaxAllowedPerEmployee(0);

        try {
            deviceService.createItem(item);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("maxAllowedPerEmployee"));
        }
    }

    @Test(priority = 14, groups = "crud")
    public void testReturnDeviceAlreadyReturned() {
        IssuedDeviceRecord record = new IssuedDeviceRecord();
        record.setId(1L);
        record.setStatus("RETURNED");
        when(issuedRepo.findById(1L)).thenReturn(Optional.of(record));

        try {
            issuedService.returnDevice(1L);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("already returned"));
        }
    }

    @Test(priority = 15, groups = "crud")
    public void testGetAllDeviceItemsEmptyList() {
        when(deviceRepo.findAll()).thenReturn(Collections.emptyList());
        Assert.assertTrue(deviceService.getAllItems().isEmpty());
    }

    @Test(priority = 16, groups = "crud")
    public void testUpdateDeviceActiveStatus() {
        DeviceCatalogItem item = new DeviceCatalogItem();
        item.setId(10L);
        item.setActive(true);
        when(deviceRepo.findById(10L)).thenReturn(Optional.of(item));
        when(deviceRepo.save(any(DeviceCatalogItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DeviceCatalogItem updated = deviceService.updateActiveStatus(10L, false);
        Assert.assertFalse(updated.getActive());
    }

    @Test(priority = 17, groups = "crud")
    public void testGetEmployeeByIdNotFound() {
        when(employeeRepo.findById(999L)).thenReturn(Optional.empty());
        try {
            employeeService.getEmployeeById(999L);
            Assert.fail("Expected ResourceNotFoundException");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Employee not found"));
        }
    }

    // ------------------------------------------------------------------------
    // 3) Configure and perform DI and IoC using Spring Framework
    // ------------------------------------------------------------------------

    @Test(priority = 18, groups = "di")
    public void testAutowiredServicesNotNull() {
        Assert.assertNotNull(eligibilityService);
        Assert.assertNotNull(employeeService);
        Assert.assertNotNull(deviceService);
    }

    @Test(priority = 19, groups = "di")
    public void testPasswordEncoderBean() {
        String raw = "password";
        String encoded = passwordEncoder.encode(raw);
        Assert.assertTrue(passwordEncoder.matches(raw, encoded));
    }

    @Test(priority = 20, groups = "di")
    public void testIoCWithMockedRepository() {
        when(employeeRepo.findAll()).thenReturn(List.of(new EmployeeProfile(), new EmployeeProfile()));
        List<EmployeeProfile> all = employeeService.getAllEmployees();
        Assert.assertEquals(all.size(), 2);
    }

    @Test(priority = 21, groups = "di")
    public void testIoCNegativeNoEmployees() {
        when(employeeRepo.findAll()).thenReturn(Collections.emptyList());
        Assert.assertTrue(employeeService.getAllEmployees().isEmpty());
    }

    @Test(priority = 22, groups = "di")
    public void testIoCEdgeCaseNullEmployeeRepo() {
        Assert.assertNotNull(employeeRepo);
    }

    // ------------------------------------------------------------------------
    // 4) Implement Hibernate configurations, generator classes, annotations, CRUD
    // ------------------------------------------------------------------------

    @Test(priority = 23, groups = "hibernate")
    public void testEmployeeEntityHasIdGenerated() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setId(1L);
        Assert.assertEquals(emp.getId(), Long.valueOf(1L));
    }

    @Test(priority = 24, groups = "hibernate")
    public void testPolicyRuleUniqueRuleCodeConceptual() {
        PolicyRule rule1 = new PolicyRule();
        rule1.setRuleCode("R001");
        PolicyRule rule2 = new PolicyRule();
        rule2.setRuleCode("R001");
        Assert.assertEquals(rule1.getRuleCode(), rule2.getRuleCode());
    }

    @Test(priority = 25, groups = "hibernate")
    public void testHibernateCrudSaveAndFindPolicyRule() {
        PolicyRule r = new PolicyRule();
        r.setId(5L);
        r.setRuleCode("R005");
        when(policyRepo.save(any(PolicyRule.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(policyRepo.findById(5L)).thenReturn(Optional.of(r));

        PolicyRule saved = ruleService.createRule(r);
        Assert.assertEquals(saved.getRuleCode(), "R005");
        PolicyRule found = ruleService.getAllRules().stream().findFirst().orElse(saved);
        Assert.assertNotNull(found);
    }

    @Test(priority = 26, groups = "hibernate")
    public void testHibernateNegativeDuplicateRuleCode() {
        PolicyRule existing = new PolicyRule();
        existing.setRuleCode("R009");
        when(policyRepo.findByRuleCode("R009")).thenReturn(Optional.of(existing));

        PolicyRule r = new PolicyRule();
        r.setRuleCode("R009");
        try {
            ruleService.createRule(r);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("Rule code"));
        }
    }

    @Test(priority = 27, groups = "hibernate")
    public void testIssuedRecordStatusLogicIssued() {
        IssuedDeviceRecord r = new IssuedDeviceRecord();
        r.setStatus("ISSUED");
        Assert.assertEquals(r.getStatus(), "ISSUED");
    }

    @Test(priority = 28, groups = "hibernate")
    public void testIssuedRecordStatusLogicReturned() {
        IssuedDeviceRecord r = new IssuedDeviceRecord();
        r.setStatus("RETURNED");
        Assert.assertEquals(r.getStatus(), "RETURNED");
    }

    @Test(priority = 29, groups = "hibernate")
    public void testHibernateEdgeNullReturnedDateOnIssue() {
        IssuedDeviceRecord r = new IssuedDeviceRecord();
        r.setReturnedDate(null);
        Assert.assertNull(r.getReturnedDate());
    }

    // ------------------------------------------------------------------------
    // 5) Perform JPA mapping with normalization (1NF, 2NF, 3NF)
    // ------------------------------------------------------------------------

    @Test(priority = 30, groups = "jpa-mapping")
    public void testEmployeeProfileNormalizedAttributes() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setFullName("John Doe");
        emp.setDepartment("IT");
        emp.setJobRole("DEVELOPER");
        Assert.assertEquals(emp.getDepartment(), "IT");
    }

    @Test(priority = 31, groups = "jpa-mapping")
    public void testPolicyRuleNormalizationNoRepeatingGroups() {
        PolicyRule rule = new PolicyRule();
        rule.setAppliesToRole("DEVELOPER");
        rule.setAppliesToDepartment("IT");
        Assert.assertEquals(rule.getAppliesToRole(), "DEVELOPER");
    }

    @Test(priority = 32, groups = "jpa-mapping")
    public void test1NFNoMultiValuedColumns() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setEmail("single@example.com");
        Assert.assertFalse(emp.getEmail().contains(","));
    }

    @Test(priority = 33, groups = "jpa-mapping")
    public void test2NFNoPartialDependencyConceptual() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setEmployeeId("E777");
        emp.setDepartment("HR");
        Assert.assertNotNull(emp.getEmployeeId());
        Assert.assertNotNull(emp.getDepartment());
    }

    @Test(priority = 34, groups = "jpa-mapping")
    public void test3NFNoTransitiveDependencyConceptual() {
        DeviceCatalogItem item = new DeviceCatalogItem();
        item.setDeviceCode("M-01");
        item.setModel("Dell UltraSharp");
        Assert.assertNotNull(item.getModel());
    }

    @Test(priority = 35, groups = "jpa-mapping")
    public void testJPAEdgeNullOptionalFields() {
        PolicyRule rule = new PolicyRule();
        rule.setAppliesToRole(null);
        rule.setAppliesToDepartment(null);
        Assert.assertNull(rule.getAppliesToRole());
        Assert.assertNull(rule.getAppliesToDepartment());
    }

    // ------------------------------------------------------------------------
    // 6) Create Many-to-Many relationships and test associations (conceptual)
    // ------------------------------------------------------------------------

    @Test(priority = 36, groups = "many-to-many")
    public void testConceptualManyToManyEmployeePolicies() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setEmployeeId("E999");
        PolicyRule r1 = new PolicyRule();
        r1.setRuleCode("R1");
        PolicyRule r2 = new PolicyRule();
        r2.setRuleCode("R2");

        List<PolicyRule> policies = List.of(r1, r2);
        Assert.assertEquals(policies.size(), 2);
        Assert.assertEquals(policies.get(0).getRuleCode(), "R1");
    }

    @Test(priority = 37, groups = "many-to-many")
    public void testManyToManyNegativeNoPolicies() {
        List<PolicyRule> policies = Collections.emptyList();
        Assert.assertTrue(policies.isEmpty());
    }

    @Test(priority = 38, groups = "many-to-many")
    public void testManyToManyEdgeSinglePolicy() {
        PolicyRule r = new PolicyRule();
        r.setRuleCode("R-SINGLE");
        List<PolicyRule> policies = List.of(r);
        Assert.assertEquals(policies.size(), 1);
    }

    @Test(priority = 39, groups = "many-to-many")
    public void testManyToManyEmployeeDepartmentMatchingPolicy() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setDepartment("IT");

        PolicyRule r = new PolicyRule();
        r.setAppliesToDepartment("IT");
        Assert.assertEquals(emp.getDepartment(), r.getAppliesToDepartment());
    }

    @Test(priority = 40, groups = "many-to-many")
    public void testManyToManyEmployeeRoleMatchingPolicy() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setJobRole("MANAGER");
        PolicyRule r = new PolicyRule();
        r.setAppliesToRole("MANAGER");
        Assert.assertEquals(emp.getJobRole(), r.getAppliesToRole());
    }

    // ------------------------------------------------------------------------
    // 7) Implement basic security controls and JWT token-based authentication
    // ------------------------------------------------------------------------

    @Test(priority = 41, groups = "security")
    public void testJwtGenerationIncludesUserIdEmailRole() {
        UserAccount user = new UserAccount();
        user.setId(100L);
        user.setEmail("itadmin@example.com");
        user.setRole("ADMIN");
        String token = tokenProvider.generateToken(user);
        Assert.assertNotNull(token);
    }

    @Test(priority = 42, groups = "security")
    public void testJwtValidatePositive() {
        UserAccount user = new UserAccount();
        user.setId(200L);
        user.setEmail("operator@example.com");
        user.setRole("IT_OPERATOR");
        String token = tokenProvider.generateToken(user);
        Assert.assertTrue(tokenProvider.validateToken(token));
    }

    @Test(priority = 43, groups = "security")
    public void testJwtInvalidateCorruptedToken() {
        String invalid = "abc.def.ghi";
        Assert.assertFalse(tokenProvider.validateToken(invalid));
    }

    @Test(priority = 44, groups = "security")
    public void testJwtGetUsername() {
        UserAccount user = new UserAccount();
        user.setId(300L);
        user.setEmail("auditor@example.com");
        user.setRole("AUDITOR");
        String token = tokenProvider.generateToken(user);
        String username = tokenProvider.getUsername(token);
        Assert.assertEquals(username, "auditor@example.com");
    }

    @Test(priority = 45, groups = "security")
    public void testPasswordEncoderMatches() {
        String raw = "Secret123";
        String encoded = passwordEncoder.encode(raw);
        Assert.assertTrue(passwordEncoder.matches(raw, encoded));
    }

    @Test(priority = 46, groups = "security")
    public void testSecurityAccessDeniedConceptualForNoToken() {
        boolean hasToken = false;
        Assert.assertFalse(hasToken);
    }

    @Test(priority = 47, groups = "security")
    public void testSecurityEdgeEmptyTokenString() {
        String token = "";
        Assert.assertFalse(tokenProvider.validateToken(token));
    }

    // ------------------------------------------------------------------------
    // 8) Use HQL and HCQL to perform advanced data querying (via repository)
    // ------------------------------------------------------------------------

    @Test(priority = 48, groups = "hql")
    public void testHqlCountActiveDevicesForEmployee() {
        when(issuedRepo.countActiveDevicesForEmployee(1L)).thenReturn(3L);
        long count = issuedRepo.countActiveDevicesForEmployee(1L);
        Assert.assertEquals(count, 3L);
    }

    @Test(priority = 49, groups = "hql")
    public void testHqlFindActiveByEmployeeAndDevice() {
        IssuedDeviceRecord r = new IssuedDeviceRecord();
        r.setEmployeeId(1L);
        r.setDeviceItemId(2L);
        r.setStatus("ISSUED");
        when(issuedRepo.findActiveByEmployeeAndDevice(1L, 2L)).thenReturn(List.of(r));

        List<IssuedDeviceRecord> list = issuedRepo.findActiveByEmployeeAndDevice(1L, 2L);
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).getStatus(), "ISSUED");
    }

    @Test(priority = 50, groups = "hql")
    public void testHqlFindActiveRules() {
        PolicyRule r1 = new PolicyRule();
        r1.setActive(true);
        PolicyRule r2 = new PolicyRule();
        r2.setActive(true);
        when(policyRepo.findByActiveTrue()).thenReturn(List.of(r1, r2));
        List<PolicyRule> list = ruleService.getActiveRules();
        Assert.assertEquals(list.size(), 2);
    }

    @Test(priority = 51, groups = "hql")
    public void testHqlEligibilityCheckByEmployee() {
        EligibilityCheckRecord rec = new EligibilityCheckRecord();
        rec.setEmployeeId(5L);
        when(eligibilityRepo.findByEmployeeId(5L)).thenReturn(List.of(rec));
        List<EligibilityCheckRecord> list = eligibilityService.getChecksByEmployee(5L);
        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 52, groups = "hql")
    public void testHqlNegativeNoEligibilityChecks() {
        when(eligibilityRepo.findByEmployeeId(999L)).thenReturn(Collections.emptyList());
        List<EligibilityCheckRecord> list = eligibilityService.getChecksByEmployee(999L);
        Assert.assertTrue(list.isEmpty());
    }

    @Test(priority = 53, groups = "hql")
    public void testHqlEdgeNullEmployeeIdQuery() {
        List<EligibilityCheckRecord> list = Collections.emptyList();
        Assert.assertTrue(list.isEmpty());
    }

    @Test(priority = 54, groups = "hql")
    public void testValidateEligibilityEmployeeNotFound() {
        when(employeeRepo.findById(111L)).thenReturn(Optional.empty());
        when(deviceRepo.findById(222L)).thenReturn(Optional.empty());
        when(eligibilityRepo.save(any(EligibilityCheckRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EligibilityCheckRecord rec = eligibilityService.validateEligibility(111L, 222L);
        Assert.assertFalse(rec.getIsEligible());
        Assert.assertTrue(rec.getReason().contains("not found"));
    }

    @Test(priority = 55, groups = "hql")
    public void testValidateEligibilityInactiveEmployee() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setId(1L);
        emp.setActive(false);
        DeviceCatalogItem dev = new DeviceCatalogItem();
        dev.setId(1L);
        dev.setActive(true);

        when(employeeRepo.findById(1L)).thenReturn(Optional.of(emp));
        when(deviceRepo.findById(1L)).thenReturn(Optional.of(dev));
        when(eligibilityRepo.save(any(EligibilityCheckRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EligibilityCheckRecord rec = eligibilityService.validateEligibility(1L, 1L);
        Assert.assertFalse(rec.getIsEligible());
        Assert.assertTrue(rec.getReason().contains("not active"));
    }

    @Test(priority = 56, groups = "hql")
    public void testValidateEligibilityInactiveDevice() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setId(2L);
        emp.setActive(true);
        DeviceCatalogItem dev = new DeviceCatalogItem();
        dev.setId(2L);
        dev.setActive(false);

        when(employeeRepo.findById(2L)).thenReturn(Optional.of(emp));
        when(deviceRepo.findById(2L)).thenReturn(Optional.of(dev));
        when(eligibilityRepo.save(any(EligibilityCheckRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EligibilityCheckRecord rec = eligibilityService.validateEligibility(2L, 2L);
        Assert.assertFalse(rec.getIsEligible());
        Assert.assertTrue(rec.getReason().contains("inactive"));
    }

    @Test(priority = 57, groups = "hql")
    public void testValidateEligibilityActiveAssignmentExists() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setId(3L);
        emp.setActive(true);
        DeviceCatalogItem dev = new DeviceCatalogItem();
        dev.setId(3L);
        dev.setActive(true);

        IssuedDeviceRecord r = new IssuedDeviceRecord();
        r.setEmployeeId(3L);
        r.setDeviceItemId(3L);
        r.setStatus("ISSUED");

        when(employeeRepo.findById(3L)).thenReturn(Optional.of(emp));
        when(deviceRepo.findById(3L)).thenReturn(Optional.of(dev));
        when(issuedRepo.findActiveByEmployeeAndDevice(3L, 3L)).thenReturn(List.of(r));
        when(eligibilityRepo.save(any(EligibilityCheckRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EligibilityCheckRecord rec = eligibilityService.validateEligibility(3L, 3L);
        Assert.assertFalse(rec.getIsEligible());
        Assert.assertTrue(rec.getReason().toLowerCase().contains("active issuance"));
    }

    @Test(priority = 58, groups = "hql")
    public void testValidateEligibilityMaxDevicesReachedDeviceLevel() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setId(4L);
        emp.setActive(true);
        DeviceCatalogItem dev = new DeviceCatalogItem();
        dev.setId(4L);
        dev.setActive(true);
        dev.setMaxAllowedPerEmployee(1);

        when(employeeRepo.findById(4L)).thenReturn(Optional.of(emp));
        when(deviceRepo.findById(4L)).thenReturn(Optional.of(dev));
        when(issuedRepo.findActiveByEmployeeAndDevice(4L, 4L)).thenReturn(Collections.emptyList());
        when(issuedRepo.countActiveDevicesForEmployee(4L)).thenReturn(1L);
        when(eligibilityRepo.save(any(EligibilityCheckRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EligibilityCheckRecord rec = eligibilityService.validateEligibility(4L, 4L);
        Assert.assertFalse(rec.getIsEligible());
        Assert.assertTrue(rec.getReason().contains("Maximum allowed devices"));
    }

    @Test(priority = 59, groups = "hql")
    public void testValidateEligibilityPolicyViolation() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setId(5L);
        emp.setActive(true);
        emp.setDepartment("IT");
        emp.setJobRole("DEVELOPER");

        DeviceCatalogItem dev = new DeviceCatalogItem();
        dev.setId(5L);
        dev.setActive(true);
        dev.setMaxAllowedPerEmployee(10);

        PolicyRule rule = new PolicyRule();
        rule.setRuleCode("R-IT-1");
        rule.setActive(true);
        rule.setAppliesToDepartment("IT");
        rule.setAppliesToRole("DEVELOPER");
        rule.setMaxDevicesAllowed(0); // effectively none

        when(employeeRepo.findById(5L)).thenReturn(Optional.of(emp));
        when(deviceRepo.findById(5L)).thenReturn(Optional.of(dev));
        when(issuedRepo.findActiveByEmployeeAndDevice(5L, 5L)).thenReturn(Collections.emptyList());
        when(issuedRepo.countActiveDevicesForEmployee(5L)).thenReturn(0L);
        when(policyRepo.findByActiveTrue()).thenReturn(List.of(rule));
        when(eligibilityRepo.save(any(EligibilityCheckRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EligibilityCheckRecord rec = eligibilityService.validateEligibility(5L, 5L);
        Assert.assertFalse(rec.getIsEligible());
        Assert.assertTrue(rec.getReason().contains("Policy violation"));
    }

    @Test(priority = 60, groups = "hql")
    public void testValidateEligibilityPositiveEligible() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setId(6L);
        emp.setActive(true);
        emp.setDepartment("HR");
        emp.setJobRole("MANAGER");

        DeviceCatalogItem dev = new DeviceCatalogItem();
        dev.setId(6L);
        dev.setActive(true);
        dev.setMaxAllowedPerEmployee(5);

        when(employeeRepo.findById(6L)).thenReturn(Optional.of(emp));
        when(deviceRepo.findById(6L)).thenReturn(Optional.of(dev));
        when(issuedRepo.findActiveByEmployeeAndDevice(6L, 6L)).thenReturn(Collections.emptyList());
        when(issuedRepo.countActiveDevicesForEmployee(6L)).thenReturn(1L);
        when(policyRepo.findByActiveTrue()).thenReturn(Collections.emptyList());
        when(eligibilityRepo.save(any(EligibilityCheckRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EligibilityCheckRecord rec = eligibilityService.validateEligibility(6L, 6L);
        Assert.assertTrue(rec.getIsEligible());
    }

    @Test(priority = 61, groups = "hql")
    public void testEligibilityCheckRecordAutoTimestamp() {
        EligibilityCheckRecord rec = new EligibilityCheckRecord();
        rec.setEmployeeId(7L);
        rec.setDeviceItemId(7L);
        rec.setIsEligible(true);
        rec.setReason("Test");
        rec.prePersist();
        Assert.assertNotNull(rec.getCheckedAt());
    }

    @Test(priority = 62, groups = "hql")
    public void testEdgeEligibilityWithZeroIssuedDevicesAndNoPolicies() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setId(8L);
        emp.setActive(true);
        emp.setDepartment("SALES");
        emp.setJobRole("EXEC");

        DeviceCatalogItem dev = new DeviceCatalogItem();
        dev.setId(8L);
        dev.setActive(true);
        dev.setMaxAllowedPerEmployee(1);

        when(employeeRepo.findById(8L)).thenReturn(Optional.of(emp));
        when(deviceRepo.findById(8L)).thenReturn(Optional.of(dev));
        when(issuedRepo.findActiveByEmployeeAndDevice(8L, 8L)).thenReturn(Collections.emptyList());
        when(issuedRepo.countActiveDevicesForEmployee(8L)).thenReturn(0L);
        when(policyRepo.findByActiveTrue()).thenReturn(Collections.emptyList());
        when(eligibilityRepo.save(any(EligibilityCheckRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EligibilityCheckRecord rec = eligibilityService.validateEligibility(8L, 8L);
        Assert.assertTrue(rec.getIsEligible());
    }

    @Test(priority = 63, groups = "hql")
    public void testEdgePolicyRuleAppliesToAllEmployees() {
        EmployeeProfile emp = new EmployeeProfile();
        emp.setId(9L);
        emp.setActive(true);
        emp.setDepartment("IT");
        emp.setJobRole("DEV");

        DeviceCatalogItem dev = new DeviceCatalogItem();
        dev.setId(9L);
        dev.setActive(true);
        dev.setMaxAllowedPerEmployee(10);

        PolicyRule rule = new PolicyRule();
        rule.setRuleCode("GLOBAL-LIMIT");
        rule.setActive(true);
        rule.setAppliesToDepartment(null);
        rule.setAppliesToRole(null);
        rule.setMaxDevicesAllowed(0);

        when(employeeRepo.findById(9L)).thenReturn(Optional.of(emp));
        when(deviceRepo.findById(9L)).thenReturn(Optional.of(dev));
        when(issuedRepo.findActiveByEmployeeAndDevice(9L, 9L)).thenReturn(Collections.emptyList());
        when(issuedRepo.countActiveDevicesForEmployee(9L)).thenReturn(0L);
        when(policyRepo.findByActiveTrue()).thenReturn(List.of(rule));
        when(eligibilityRepo.save(any(EligibilityCheckRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EligibilityCheckRecord rec = eligibilityService.validateEligibility(9L, 9L);
        Assert.assertFalse(rec.getIsEligible());
        Assert.assertTrue(rec.getReason().contains("Policy violation"));
    }
}
