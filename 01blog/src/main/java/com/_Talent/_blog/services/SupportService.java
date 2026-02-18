package com._Talent._blog.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com._Talent._blog.dto.SupportRequest;

@Service
public class SupportService {

    @Value("${spring.mail.username}")
    private String ToEmail;
    
    private final JavaMailSender mailSender;

    SupportService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void sendSupportEmail(SupportRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(request.getEmail());
        message.setTo(ToEmail); 
        message.setSubject("[Support] " + request.getSubject() + " - " + request.getCategory());
        message.setText(
            "New Support Request:\n\n" +
            "Name: " + request.getName() + "\n" +
            "Email: " + request.getEmail() + "\n" +
            "Category: " + request.getCategory() + "\n" +
            "Subject: " + request.getSubject() + "\n\n" +
            "Message:\n" + request.getMessage() + "\n\n" +
            "---\n" +
            "This is an automated message from the support form."
        );
        
        mailSender.send(message);
    }
    
    public void sendConfirmationEmail(String toEmail, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(ToEmail);
        message.setTo(toEmail);
        message.setSubject("We've Received Your Support Request");
        message.setText(
            "Hello " + userName + ",\n\n" +
            "Thank you for contacting our support team. We've received your message and will get back to you within 24 hours.\n\n" +
            "For urgent matters.\n\n" +
            "Best regards,\n" +
            "01Blog Support Team"
        );
        
        mailSender.send(message);
    }
}
