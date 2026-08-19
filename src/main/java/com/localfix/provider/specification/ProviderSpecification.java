package com.localfix.provider.specification;

import com.localfix.provider.entity.Provider;
import com.localfix.provider.services.entity.ProviderServiceMapping;
import com.localfix.service.entity.Service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ProviderSpecification {

    private ProviderSpecification() {
    }

    public static Specification<Provider> search(
            String keyword,
            String city,
            String state,
            UUID serviceId,
            UUID categoryId) {

        return (root, query, cb) -> {

            query.distinct(true);

            List<Predicate> predicates =
                    new ArrayList<>();

            /*
             * Only public providers
             */
            predicates.add(
                    cb.isTrue(root.get("active"))
            );

            predicates.add(
                    cb.isTrue(root.get("verified"))
            );


            /*
             * Keyword search
             */
            if (keyword != null &&
                    !keyword.isBlank()) {

                String search =
                        "%" +
                                keyword.trim().toLowerCase() +
                                "%";

                Predicate businessName =
                        cb.like(
                                cb.lower(
                                        root.get("businessName")
                                ),
                                search
                        );

                Predicate description =
                        cb.like(
                                cb.lower(
                                        root.get("description")
                                ),
                                search
                        );

                predicates.add(
                        cb.or(
                                businessName,
                                description
                        )
                );
            }


            /*
             * City filter
             */
            if (city != null &&
                    !city.isBlank()) {

                predicates.add(
                        cb.equal(
                                cb.lower(
                                        root.get("city")
                                ),
                                city.trim().toLowerCase()
                        )
                );
            }


            /*
             * State filter
             */
            if (state != null &&
                    !state.isBlank()) {

                predicates.add(
                        cb.equal(
                                cb.lower(
                                        root.get("state")
                                ),
                                state.trim().toLowerCase()
                        )
                );
            }


            /*
             * Service / Category filter
             */
            if (serviceId != null ||
                    categoryId != null) {

                Join<Provider, ProviderServiceMapping>
                        providerServices =
                        root.join(
                                "providerServices",
                                JoinType.INNER
                        );

                predicates.add(
                        cb.isTrue(
                                providerServices.get("active")
                        )
                );

                Join<ProviderServiceMapping, Service>
                        service =
                        providerServices.join(
                                "service",
                                JoinType.INNER
                        );

                predicates.add(
                        cb.isTrue(
                                service.get("active")
                        )
                );


                if (serviceId != null) {

                    predicates.add(
                            cb.equal(
                                    service.get("id"),
                                    serviceId
                            )
                    );
                }


                if (categoryId != null) {

                    predicates.add(
                            cb.equal(
                                    service
                                            .get("category")
                                            .get("id"),
                                    categoryId
                            )
                    );
                }
            }

            return cb.and(
                    predicates.toArray(
                            new Predicate[0]
                    )
            );
        };
    }
}