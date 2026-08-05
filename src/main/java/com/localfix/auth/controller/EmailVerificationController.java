package com.localfix.auth.controller;

import com.localfix.auth.emailverification.dto.VerifyEmailRequest;
import com.localfix.auth.emailverification.service.EmailVerificationService;
import com.localfix.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService service;

    @PostMapping("/send-verification")
    public ResponseEntity<ApiResponse<Void>> sendOtp() {

        service.sendVerificationOtp();

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Verification OTP sent successfully.")
                        .build()
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(
            @Valid @RequestBody VerifyEmailRequest request) {

        service.verifyOtp(request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Email verified successfully.")
                        .build()
        );
    }

    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<Void>> resend() {

        service.resendOtp();

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("OTP resent successfully.")
                        .build()
        );
    }
}