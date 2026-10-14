package com.homycarev1.ecommerce.dao;

import com.homycarev1.ecommerce.entity.PricingPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "pricingPlans", path = "pricing-plans")
public interface PricingPlanRepository extends JpaRepository<PricingPlan, Long> {
    Page<PricingPlan> findByActiveTrueOrderByDisplayOrderAsc(Pageable pageable);
}
