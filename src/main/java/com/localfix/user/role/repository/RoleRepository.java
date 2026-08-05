package com.localfix.user.role.repository;

import com.localfix.common.enums.RoleType;
import com.localfix.user.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(RoleType name);
    boolean existsByName(RoleType name);

}
