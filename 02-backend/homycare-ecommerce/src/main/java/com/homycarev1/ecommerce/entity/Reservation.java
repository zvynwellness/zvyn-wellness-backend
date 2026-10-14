package com.homycarev1.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Getter
@Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private AppUser customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private WellnessService service;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeProfile employee;

    @Column(name = "preferred_date_time", nullable = false)
    private LocalDateTime preferredDateTime;

    @Column(length = 1000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.PENDING;

    @CreationTimestamp
    private Instant dateCreated;
}
