package com.homycarev1.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "purpose_content")
@Getter
@Setter
public class PurposeContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1200)
    private String brandPhilosophy;

    @Column(nullable = false, length = 1200)
    private String personalApproach;

    @Column(nullable = false, length = 1200)
    private String coreValues;

    @Column(nullable = false)
    private boolean active = true;
}
