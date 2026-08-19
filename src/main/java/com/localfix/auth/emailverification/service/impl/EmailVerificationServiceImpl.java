package com.localfix.auth.emailverification.service.impl;

import com.localfix.auth.email.EmailService;
import com.localfix.auth.emailverification.dto.VerifyEmailRequest;
import com.localfix.auth.emailverification.entity.EmailVerification;
import com.localfix.auth.emailverification.repository.EmailVerificationRepository;
import com.localfix.auth.emailverification.service.EmailVerificationService;
import com.localfix.auth.emailverification.util.OtpGenerator;
import com.localfix.common.config.SecurityUtils;
import com.localfix.common.exception.InvalidOtpException;
import com.localfix.common.exception.OtpExpiredException;
import com.localfix.common.exception.ResourceAlreadyExistsException;
import com.localfix.common.exception.ResourceNotFoundException;
import com.localfix.user.entity.User;
import com.localfix.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;
    private final OtpGenerator otpGenerator;

    @Override
    public void sendVerificationOtp() {
        // Get logged-in user email
        String email = SecurityUtils.getCurrentUsername();
        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
        // Already verified?
        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            log.error("=============Email is already verified.=============");
            throw new ResourceAlreadyExistsException("Email is already verified.");
        }
        // Remove old OTP if present
        emailVerificationRepository.findByUser(user)
                .ifPresent(emailVerificationRepository::delete);
        // Generate OTP
        String otp = otpGenerator.generateOtp();
        // Create entity
        EmailVerification verification = new EmailVerification();
        verification.setUser(user);
        verification.setOtp(otp);
        verification.setVerified(false);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        // Save
        emailVerificationRepository.save(verification);
        // Send Email
        String subject = "LocalFix Email Verification";
        String body = """
            Hello %s,

            Welcome to LocalFix!

            Your email verification code is:

            %s

            This OTP is valid for 5 minutes.

            If you did not request this email, please ignore it.

            Regards,
            LocalFix Team
            """.formatted(user.getFirstName(), otp);

        emailService.sendSimpleEmail(
                user.getEmail(),
                subject,
                body
        );
    }

    @Override
    @Transactional
    public void verifyOtp(VerifyEmailRequest request) {

        String email = SecurityUtils.getCurrentUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            log.error("=============Email already verified.=============");
            throw new ResourceAlreadyExistsException("Email already verified.");
        }

        EmailVerification verification =
                emailVerificationRepository.findByUserAndVerifiedFalse(user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Verification request not found."));

        // Check expiry
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("=============OTP has expired.=============");
            throw new OtpExpiredException("OTP has expired.");
        }

        // Check OTP
        if (!verification.getOtp().equals(request.otp())) {
            log.error("=============Invalid OTP.=============");
            throw new InvalidOtpException("Invalid OTP.");
        }

        // Update verification
        verification.setVerified(true);

        // Update user
        user.setEmailVerified(true);

        emailVerificationRepository.save(verification);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resendOtp() {

        String email = SecurityUtils.getCurrentUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            log.info("=============Email already verified.=============");
            throw new ResourceAlreadyExistsException("Email already verified.");
        }

        // Delete existing OTP
        emailVerificationRepository.findByUser(user)
                .ifPresent(emailVerificationRepository::delete);

        emailVerificationRepository.flush();

        // Generate new OTP
        String otp = otpGenerator.generateOtp();

        EmailVerification verification = new EmailVerification();

        verification.setUser(user);
        verification.setOtp(otp);
        verification.setVerified(false);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        emailVerificationRepository.save(verification);

        String subject = "LocalFix - New Verification OTP";

        String body = """
            Hello %s,

            As requested, here is your new verification code.

            OTP : %s

            This OTP is valid for 5 minutes.

            Regards,
            LocalFix Team
            """.formatted(user.getFirstName(), otp);

        emailService.sendSimpleEmail(
                user.getEmail(),
                subject,
                body
        );
        log.info("=============new verification code sent.=============");
    }
}