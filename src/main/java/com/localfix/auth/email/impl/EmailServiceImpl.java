package com.localfix.auth.email.impl;
//
//import com.localfix.auth.email.EmailService;
//import lombok.AllArgsConstructor;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//@AllArgsConstructor
//public class EmailServiceImpl implements EmailService {
//
//    private final JavaMailSender jms;
//
//    @Override
//    public void sendSimpleEmail(String to,String subject,String body) {
//
//        SimpleMailMessage mail=new SimpleMailMessage();
//        mail.setTo(to);
//        mail.setSubject(subject);
//        mail.setText(body);
//
//        jms.send(mail);
//
//    }
//}
//---------------------------------------------------Production logic------------------------------------------------------------


import com.localfix.auth.email.EmailService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final Resend resend;
    private final String from;

    public EmailServiceImpl(
            @Value("${resend.api-key}") String apiKey,
            @Value("${mail.from}") String from) {

        this.resend = new Resend(apiKey);
        this.from = from;
    }

    @Override
    public void sendSimpleEmail(
            String to,
            String subject,
            String body) {

        CreateEmailOptions params =
                CreateEmailOptions.builder()
                        .from(from)
                        .to(to)
                        .subject(subject)
                        .html(body)
                        .build();

        try {

            CreateEmailResponse response =
                    resend.emails().send(params);

            System.out.println(
                    "Email sent successfully. Resend ID: "
                            + response.getId()
            );

        } catch (ResendException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(
                    "Failed to send email.",
                    e
            );
        }
    }
}