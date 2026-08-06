package com.localfix.auth.forgotpassword.repository;

import com.localfix.auth.forgotpassword.entity.PasswordReset;
import com.localfix.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset,Long> {

    Optional<PasswordReset> findByUser(User user);

    void deleteByUser(User user);

    Optional<PasswordReset> findByUserAndVerifiedTrue(User user);

    Optional<PasswordReset> findByUserAndVerifiedFalse(User user);

}
