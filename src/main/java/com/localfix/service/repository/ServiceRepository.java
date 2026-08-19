package com.localfix.service.repository;

import com.localfix.service.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServiceRepository
        extends JpaRepository<Service, UUID> {

    boolean existsByCategoryIdAndNameIgnoreCase(
            UUID categoryId,
            String name
    );

    boolean existsByCategoryIdAndNameIgnoreCaseAndIdNot(
            UUID categoryId,
            String name,
            UUID id
    );

    Optional<Service> findByCategoryIdAndNameIgnoreCase(
            UUID categoryId,
            String name
    );

    Optional<Service> findByIdAndActiveTrue(UUID id);

    Page<Service> findAllByActiveTrue(
            Pageable pageable
    );

    Page<Service> findByNameContainingIgnoreCaseAndActiveTrue(
            String name,
            Pageable pageable
    );

    Page<Service> findByCategoryIdAndActiveTrue(
            UUID categoryId,
            Pageable pageable
    );
}