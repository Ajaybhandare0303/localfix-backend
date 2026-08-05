package com.localfix.auth.forgotpassword.service;

import com.localfix.auth.forgotpassword.dto.ForgotPasswordRequest;
import com.localfix.auth.forgotpassword.dto.ResetPasswordRequest;
import com.localfix.auth.forgotpassword.dto.VerifyResetOtpRequest;

public interface PasswordResetService {

    void forgotPassword(ForgotPasswordRequest request);

    void verifyOtp(VerifyResetOtpRequest request);

    void resetPassword(ResetPasswordRequest request);
}