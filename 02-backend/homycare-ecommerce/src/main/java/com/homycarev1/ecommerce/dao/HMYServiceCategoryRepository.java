package com.homycarev1.ecommerce.dao;
import com.homycarev1.ecommerce.entity.HMYServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin("http://localhost:4300")
@RepositoryRestResource(collectionResourceRel = "HMYServiceCategory", path = "hmyservice-category")


public interface HMYServiceCategoryRepository extends JpaRepository<HMYServiceCategory, Long> {
}
