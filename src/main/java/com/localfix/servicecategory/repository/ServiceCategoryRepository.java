package com.localfix.servicecategory.repository;

import com.localfix.servicecategory.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceCategoryRepository
        extends JpaRepository<ServiceCategory, UUID> {

    boolean existsByNameIgnoreCase(String name);

    Optional<ServiceCategory> findByNameIgnoreCase(String name);

    List<ServiceCategory> findAllByActiveTrue();

    Optional<ServiceCategory> findByIdAndActiveTrue(UUID id);
}