package com.homycarev1.ecommerce.dao;

import com.homycarev1.ecommerce.entity.MonthlyService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface MonthlyServiceRepository extends JpaRepository<MonthlyService, Long> {
    List<MonthlyService> findAllByOrderByServiceMonthAsc();
    long countByActiveTrue();
    boolean existsByServiceId(Long serviceId);
    void deleteByServiceId(Long serviceId);
}
