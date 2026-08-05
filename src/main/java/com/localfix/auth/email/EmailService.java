package com.localfix.auth.email;

public interface EmailService {

        void sendSimpleEmail(
                String to,
                String subject,
                String body
        );

    }
