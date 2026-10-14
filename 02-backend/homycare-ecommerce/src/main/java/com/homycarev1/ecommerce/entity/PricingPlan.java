package com.homycarev1.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "pricing_plan")
@Getter
@Setter
public class PricingPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private Integer sessionCount;

    @Column(precision = 10, scale = 2)
    private BigDecimal priceAmount;

    @Column(nullable = false, length = 3)
    private String currency = "JPY";

    @Column(nullable = false, length = 80)
    private String displayPrice;

    @Column(nullable = false, length = 500)
    private String offlinePaymentNotice;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private boolean active = true;
}
