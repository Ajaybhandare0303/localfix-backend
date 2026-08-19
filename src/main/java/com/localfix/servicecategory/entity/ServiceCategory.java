package com.localfix.servicecategory.entity;

import com.localfix.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_categories")
public class ServiceCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 255)
    private String icon;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}