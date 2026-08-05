package com.localfix.auth.emailverification.repository;

import com.localfix.auth.emailverification.entity.EmailVerification;
import com.localfix.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationRepository
        extends JpaRepository<EmailVerification, UUID> {

    Optional<EmailVerification> findByUser(User user);

    void deleteByUser(User user);

    Optional<EmailVerification> findByUserAndVerifiedFalse(User user);
}