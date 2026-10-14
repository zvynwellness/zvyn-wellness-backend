package com.homycarev1.ecommerce.dao;

import com.homycarev1.ecommerce.entity.WellnessService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "wellnessServices", path = "wellness-services")
public interface WellnessServiceRepository extends JpaRepository<WellnessService, Long> {
    Page<WellnessService> findByActiveTrueOrderByDisplayOrderAsc(Pageable pageable);
    Optional<WellnessService> findBySlug(@Param("slug") String slug);
    List<WellnessService> findByInstructorIdAndActiveTrueOrderByScheduledDateTimeAsc(Long instructorId);
}
