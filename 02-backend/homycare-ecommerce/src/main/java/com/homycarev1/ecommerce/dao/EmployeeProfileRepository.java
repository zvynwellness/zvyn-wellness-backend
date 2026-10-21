package com.homycarev1.ecommerce.dao;

import com.homycarev1.ecommerce.entity.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

// Employee profiles are exposed through the authenticated admin/employee DTOs only.
// Exporting this repository would serialize the linked AppUser, including passwordHash.
@RepositoryRestResource(exported = false)
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {
    Optional<EmployeeProfile> findByUserEmailIgnoreCase(String email);
}
