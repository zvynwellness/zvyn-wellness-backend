package com.homycarev1.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wellness_service", uniqueConstraints = @UniqueConstraint(name = "uk_wellness_service_slug", columnNames = "slug"))
@Getter
@Setter
public class WellnessService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 140)
    private String slug;

    @Column(nullable = false, length = 1600)
    private String description;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Integer customerCapacity = 10;

    @Column(nullable = false)
    private Integer employeeCapacity = 5;

    @Column(nullable = false, length = 255)
    private String location = "To be confirmed";

    @Column(name = "google_maps_url", length = 1000)
    private String googleMapsUrl;

    @Column(precision = 10, scale = 2)
    private BigDecimal priceAmount;

    @Column(nullable = false, length = 3)
    private String currency = "JPY";

    @Column(name = "scheduled_date_time")
    private LocalDateTime scheduledDateTime;

    @ManyToOne
    @JoinColumn(name = "instructor_employee_id")
    private EmployeeProfile instructor;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    private Instant dateCreated;

    @UpdateTimestamp
    private Instant lastUpdated;
}
