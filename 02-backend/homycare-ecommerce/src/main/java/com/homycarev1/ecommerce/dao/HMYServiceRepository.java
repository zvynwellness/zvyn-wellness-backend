package com.homycarev1.ecommerce.dao;
import com.homycarev1.ecommerce.entity.HMYService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin("http://localhost:4300")
@RepositoryRestResource(collectionResourceRel = "HMYService", path = "hmyservice")
public interface HMYServiceRepository extends JpaRepository <HMYService, Long> {
    Page<HMYService> findByCategoryId(@Param("id") Long id, Pageable pageable);

}
