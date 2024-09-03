package com.eCommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void send(String sendTo, String subject, String body) {

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("bhavya.shah313@outlook.com");
        email.setTo(sendTo);
        email.setSubject(subject);
        email.setText(body);

        javaMailSender.send(email);

    }

}
