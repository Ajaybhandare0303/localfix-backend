package com.localfix.provider.repository;

import com.localfix.provider.entity.Provider;
import com.localfix.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProviderRepository
        extends JpaRepository<Provider, UUID>,
        JpaSpecificationExecutor<Provider>
{

    Optional<Provider> findByUserId(UUID userId);

    Optional<Provider> findByUserIdAndActiveTrue(UUID userId);

    boolean existsByUserId(UUID userId);

    boolean existsByBusinessNameIgnoreCase(String businessName);

    Page<Provider> findAllByActiveTrueAndVerifiedTrue(
            Pageable pageable
    );

    Optional<Provider> findByIdAndActiveTrueAndVerifiedTrue(
            UUID id
    );

    @Query("""
        SELECT DISTINCT p
        FROM Provider p
        JOIN ProviderServiceMapping ps
            ON ps.provider.id = p.id
        WHERE ps.service.id = :serviceId
          AND ps.active = true
          AND p.active = true
          AND p.verified = true
        """)

    Page<Provider> findVerifiedProvidersByService(
            @Param("serviceId") UUID serviceId,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT p
        FROM Provider p
        JOIN ProviderServiceMapping ps
            ON ps.provider.id = p.id
        JOIN ps.service s
        WHERE s.category.id = :categoryId
          AND ps.active = true
          AND s.active = true
          AND p.active = true
          AND p.verified = true
        """)
    Page<Provider> findVerifiedProvidersByCategory(
            @Param("categoryId") UUID categoryId,
            Pageable pageable
    );


}