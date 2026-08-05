package com.localfix.user.repository;

import com.localfix.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("""
       SELECT u
       FROM User u
       LEFT JOIN FETCH u.roles
       WHERE u.email = :email
       """)
    Optional<User> findByEmail(@Param("email") String email);

    /*@Query("""
       SELECT u
       FROM User u
       LEFT JOIN FETCH u.roles
       WHERE u.email = :email
       """)
    boolean existsByMobileAndEmailNot(
            String mobile,
            String email
    );*/

    boolean existsByMobileAndEmailNot(String mobile, String email);

    @Query("""
       SELECT u
       FROM User u
       LEFT JOIN FETCH u.roles
       WHERE u.email = :email
       """)
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    Optional<User> findByMobile(String mobile);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);
}