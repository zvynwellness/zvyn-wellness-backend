package com.homycarev1.ecommerce.dao;

import com.homycarev1.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = {
        "http://localhost:4300",
        "https://www.zvyn-co.com"
})
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryId(@Param("id") Long id, Pageable pageable);

    Page<Product> findByNameContaining(@Param("name") String name, Pageable pageable);
}

//package com.homycarev1.ecommerce.dao;
//import com.homycarev1.ecommerce.entity.Product;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.query.Param;
////import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//import org.springframework.web.bind.annotation.CrossOrigin;
//
//@CrossOrigin("http://localhost:4300")
//
////@RepositoryRestResource(collectionResourceRel = "products", path = "product")
//public interface ProductRepository extends JpaRepository <Product, Long> {
//    Page<Product> findByCategoryId(@Param("id") Long id, Pageable pageable);
//
//    Page<Product> findByNameContaining(@Param("name") String id, Pageable pageable);
//    //CONCAT ('%',:name,'%')
//    //API to test this function : http://localhost:8080/api/products/search/findByNameContaining?name=garden
//
//}