package com.example.demo.model;

import jakarta.persistence.*;

@Model
public class DeviceCatalogItem{
    @Id
    private Long id;
    private String employeeId;
    private String fullName;
    private String email;
    private String department;
    private String jobRole;
    private Boolean active;
    private LocalDateTime createdAt;

    
}