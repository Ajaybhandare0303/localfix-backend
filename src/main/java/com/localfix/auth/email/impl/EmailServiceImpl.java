package com.localfix.auth.email.impl;

import com.localfix.auth.email.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender jms;

    @Override
    public void sendSimpleEmail(String to,String subject,String body) {

        SimpleMailMessage mail=new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(body);

        jms.send(mail);

    }
}
