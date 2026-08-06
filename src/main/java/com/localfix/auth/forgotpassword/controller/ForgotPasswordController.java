package com.localfix.auth.forgotpassword.controller;

import com.localfix.auth.forgotpassword.dto.ForgotPasswordRequest;
import com.localfix.auth.forgotpassword.dto.ResetPasswordRequest;
import com.localfix.auth.forgotpassword.dto.VerifyResetOtpRequest;
import com.localfix.auth.forgotpassword.service.PasswordResetService;
import com.localfix.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@AllArgsConstructor
public class ForgotPasswordController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.forgotPassword(request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Password reset OTP sent successfully.")
                        .build()
        );
    }


    @PostMapping("/verify-reset-otp")
    public ResponseEntity<ApiResponse<Void>> verifyResetOtp(
            @Valid @RequestBody VerifyResetOtpRequest request) {

        passwordResetService.verifyOtp(request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("OTP verified successfully.")
                        .build()
        );
    }


    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Password reset successfully.")
                        .build()
        );
    }

}
