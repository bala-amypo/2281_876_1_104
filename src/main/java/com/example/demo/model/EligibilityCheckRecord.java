package com.example.demo.model;

import jakarta.persistence.*;
// import jakarta.persistence.PrePersist;


import java.time.LocalDateTime;

@Entity
@Table(name = "eligibility_check_record")
public class EligibilityCheckRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;
    private Long deviceItemId;
    private Boolean isEligible;
    private String reason;

    private LocalDateTime checkedAt;

    // Automatically set checkedAt before insert
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // No-argument constructor
    public EligibilityCheckRecord() {
    }

    // Parameterized constructor
    public EligibilityCheckRecord(Long employeeId, Long deviceItemId,
                                  Boolean isEligible, String reason) {
        this.employeeId = employeeId;
        this.deviceItemId = deviceItemId;
        this.isEligible = isEligible;
        this.reason = reason;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getDeviceItemId() {
        return deviceItemId;
    }

    public void setDeviceItemId(Long deviceItemId) {
        this.deviceItemId = deviceItemId;
    }

    public Boolean getIsEligible() {
        return isEligible;
    }

    public void setIsEligible(Boolean isEligible) {
        this.isEligible = isEligible;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }
}
