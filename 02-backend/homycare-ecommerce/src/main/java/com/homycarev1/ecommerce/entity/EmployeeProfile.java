package com.homycarev1.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employee_profile")
@Getter
@Setter
public class EmployeeProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser user;

    @Column(length = 120)
    private String jobTitle;

    @Column(length = 120)
    private String specialty;

    @Column(length = 1000)
    private String biography;

    @Column(nullable = false)
    private boolean acceptingClients = true;
}
