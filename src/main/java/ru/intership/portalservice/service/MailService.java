package ru.intership.portalservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    public void sendNewPasswordMail(String email, String password) {
        sendMail(email, "Пароль от вашей учётной записи", getPasswordMsg(password));
    }

    public void sendMail(String toMail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(toMail);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    private String getPasswordMsg(String password) {
        return String.format("""
                Пароль от вашей учётной записи: %s
                """, password);
    }
}

