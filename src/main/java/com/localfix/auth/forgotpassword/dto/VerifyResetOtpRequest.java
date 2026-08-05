package com.localfix.auth.forgotpassword.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyResetOtpRequest(

        @NotBlank
        @Pattern(regexp="\\d{6}")
        String otp

) {}