package com._Talent._blog.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class FPService {

    private final JavaMailSender mailSender;
    

    @Value("${spring.mail.username}")
    private String fromEmail;

    public FPService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String toEmail, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your New Password");
        message.setText(
            "Hello,\n\n" +
            "Your new password is: " + newPassword + "\n\n" +
            "Please login with this password and change it immediately.\n\n" +
            "Best regards,\n" +
            "Support Team"
        );
        
        mailSender.send(message);
    }   
}