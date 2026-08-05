package com.localfix.auth.emailverification.service;

import com.localfix.auth.emailverification.dto.VerifyEmailRequest;

public interface EmailVerificationService {

    void sendVerificationOtp();

    void verifyOtp(VerifyEmailRequest request);

    void resendOtp();

}