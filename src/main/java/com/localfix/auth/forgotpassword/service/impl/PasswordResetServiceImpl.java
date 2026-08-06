package com.localfix.auth.forgotpassword.service.impl;

import com.localfix.auth.email.EmailService;
import com.localfix.auth.emailverification.util.OtpGenerator;
import com.localfix.auth.forgotpassword.dto.ForgotPasswordRequest;
import com.localfix.auth.forgotpassword.dto.ResetPasswordRequest;
import com.localfix.auth.forgotpassword.dto.VerifyResetOtpRequest;
import com.localfix.auth.forgotpassword.entity.PasswordReset;
import com.localfix.auth.forgotpassword.repository.PasswordResetRepository;
import com.localfix.auth.forgotpassword.service.PasswordResetService;
import com.localfix.common.exception.EmailNotVerifiedException;
import com.localfix.common.exception.InvalidOtpException;
import com.localfix.common.exception.OtpExpiredException;
import com.localfix.common.exception.ResourceNotFoundException;
import com.localfix.user.entity.User;
import com.localfix.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final OtpGenerator otpGenerator;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(()->
                            new ResourceNotFoundException("User Not Found ! "));


        if(!Boolean.TRUE.equals(user.getEmailVerified()))
        {
            throw new EmailNotVerifiedException("Email is not verified.");
        }

        passwordResetRepository.findByUser(user)
                .ifPresent(passwordResetRepository::delete);

        passwordResetRepository.flush();

        String otp = otpGenerator.generateOtp();

        PasswordReset passwordReset = new PasswordReset();

        passwordReset.setUser(user);
        passwordReset.setOtp(otp);
        passwordReset.setVerified(false);
        passwordReset.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        passwordResetRepository.save(passwordReset);

        String subject = "LocalFix Password Reset";

        String body = """
            Hello %s,

            We received a request to reset your password.

            OTP : %s

            This OTP is valid for 5 minutes.

            If you didn't request this, ignore this email.

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
    public void verifyOtp(VerifyResetOtpRequest request) {

        User user=userRepository.findByEmail(request.email()).orElseThrow(()->
                new ResourceNotFoundException("User not found !"));

        PasswordReset passwordReset = passwordResetRepository
                .findByUserAndVerifiedFalse(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Password reset request not found."));

        if (!passwordReset.getOtp().equals(request.otp())) {
            throw new InvalidOtpException("Invalid OTP.");
        }

        if(passwordReset.getExpiresAt().isBefore(LocalDateTime.now()))
        {
            throw new OtpExpiredException("OTP has expired.");
        }

        passwordReset.setVerified(true);
        passwordResetRepository.save(passwordReset);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        if(!request.newPassword().equals(request.confirmPassword()))
        {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        User user=userRepository.findByEmail(request.email()).orElseThrow(()->
                new ResourceNotFoundException("User not found !"));

        PasswordReset passwordReset = passwordResetRepository
                .findByUserAndVerifiedTrue(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("OTP verification not completed."));

        if (bCryptPasswordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException(
                    "New password cannot be the same as the current password.");
        }

        user.setPassword(bCryptPasswordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        passwordResetRepository.delete(passwordReset);
    }

}
