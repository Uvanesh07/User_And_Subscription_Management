package com.userms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.from}")
    private String fromEmail;

    @Value("${mail.subject.welcome}")
    private String welcomeSubject;

    @Value("${mail.subject.subscriptionExpiry}")
    private String subscriptionExpirySubject;

    @Value("${mail.subject.subscriptionExpired}")
    private String subscriptionExpiredSubject;

    @Value("${mail.subject.subscriptionRenewalReminder}")
    private String subscriptionRenewalReminderSubject;


    public void sendWelcomeEmail(String toEmail, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(welcomeSubject);
        message.setText(buildEmailBody(username, password));
        mailSender.send(message);

    }

    private String buildEmailBody(String username, String password) {
        return String.format("Dear User,\n\nWelcome to [Your Website]! We are excited to have you on board.\n\n" +
                "Here are your account details:\n\nUsername: %s\nPassword: %s\n\n" +
                "Please keep this information secure and do not share it with anyone. We recommend changing your password immediately after your first login for enhanced security.\n\n" +
                "To log in, please visit: [Login URL]\n\n" +
                "If you have any questions or need assistance, feel free to contact our support team at [Support Email/Phone Number].\n\n" +
                "Best regards,\n\n[Your Name/Your Company]\n[Your Contact Information]\n[Your Website URL]", username, password);
    }

    public void sendSubscriptionExpiryAlert(String toEmail, String username, LocalDate expiryDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subscriptionExpirySubject);
        message.setText(buildSubscriptionExpiryEmailBody(username, expiryDate));
        mailSender.send(message);
    }

    public void sendSubscriptionExpiredNotice(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subscriptionExpiredSubject);
        message.setText(buildSubscriptionExpiredEmailBody(username));
        mailSender.send(message);
    }

    public void sendSubscriptionRenewalReminder(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subscriptionRenewalReminderSubject);
        message.setText(buildSubscriptionRenewalReminderEmailBody(username));
        mailSender.send(message);
    }

    private String buildSubscriptionExpiryEmailBody(String username, LocalDate expiryDate) {
        return String.format("Dear %s,\n\nWe wanted to remind you that your subscription is set to expire on %s.\n\n" +
                "Please renew your subscription to continue enjoying our services without any interruption.\n\n" +
                "If you have any questions or need assistance, feel free to contact our support team at [Support Email/Phone Number].\n\n" +
                "Best regards,\n\n[Your Name/Your Company]\n[Your Contact Information]\n[Your Website URL]", username, expiryDate);
    }

    private String buildSubscriptionExpiredEmailBody(String username) {
        return String.format("Dear %s,\n\nWe wanted to inform you that your subscription has expired. Please renew your subscription to continue enjoying our services.\n\n" +
                "If you have any questions or need assistance, feel free to contact our support team at [Support Email/Phone Number].\n\n" +
                "Best regards,\n\n[Your Name/Your Company]\n[Your Contact Information]\n[Your Website URL]", username);
    }

    private String buildSubscriptionRenewalReminderEmailBody(String username) {
        return String.format("Dear %s,\n\nJust a friendly reminder to renew your subscription. We hope to continue providing you with our services.\n\n" +
                "If you have any questions or need assistance, feel free to contact our support team at [Support Email/Phone Number].\n\n" +
                "Best regards,\n\n[Your Name/Your Company]\n[Your Contact Information]\n[Your Website URL]", username);
    }
}

