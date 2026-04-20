package com.homycarev1.ecommerce.dao;
import com.homycarev1.ecommerce.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin("http://localhost:4300")
@RepositoryRestResource(collectionResourceRel = "productCategory", path = "product-category")

public interface ProductCategoryRepository extends JpaRepository <ProductCategory, Long> {
}
