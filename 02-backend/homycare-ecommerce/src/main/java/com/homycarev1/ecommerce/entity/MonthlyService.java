package com.homycarev1.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "monthly_service", uniqueConstraints = @UniqueConstraint(name = "uk_monthly_service_service", columnNames = "service_id"))
@Getter
@Setter
public class MonthlyService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private WellnessService service;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeProfile employee;

    @Column(name = "service_month", nullable = false)
    private LocalDate serviceMonth;

    @Column(nullable = false)
    private boolean active = true;
}
