package com.localfix.provider.services.repository;

import com.localfix.provider.services.entity.ProviderServiceMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProviderServiceRepository
        extends JpaRepository<ProviderServiceMapping, UUID> {

    boolean existsByProviderIdAndServiceId(
            UUID providerId,
            UUID serviceId
    );

    Optional<ProviderServiceMapping>
    findByProviderIdAndServiceId(
            UUID providerId,
            UUID serviceId
    );

    List<ProviderServiceMapping>
    findAllByProviderIdAndActiveTrue(
            UUID providerId
    );

    boolean existsByProviderIdAndServiceIdAndActiveTrue(
            UUID providerId,
            UUID serviceId
    );
}