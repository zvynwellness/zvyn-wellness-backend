package com.homycarev1.ecommerce.dao;

import com.homycarev1.ecommerce.entity.PurposeContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "purposeContent", path = "purpose-content")
public interface PurposeContentRepository extends JpaRepository<PurposeContent, Long> {
}
