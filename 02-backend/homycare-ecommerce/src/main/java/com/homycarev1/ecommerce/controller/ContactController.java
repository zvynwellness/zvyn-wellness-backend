package com.homycarev1.ecommerce.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String sender;

    @Value("${zvyn.contact.recipient:meerashvin@gmail.com}")
    private String recipient;

    public ContactController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping
    public Map<String, String> send(@Valid @RequestBody ContactRequest request) {
        if (sender == null || sender.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Email delivery is not configured. Set ZVYN_MAIL_USERNAME and ZVYN_MAIL_PASSWORD, then restart the backend.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(recipient);
        message.setReplyTo(request.email().trim());
        message.setSubject("ZVyn Wellness consultation enquiry" +
                (request.plan().isBlank() ? "" : " — " + request.plan().trim()));
        message.setText("Name: " + request.name().trim() +
                "\nTelephone: " + request.telephone().trim() +
                "\nEmail: " + request.email().trim() +
                "\nPreferred contact: " + request.preference().trim() +
                "\nPricing option: " + (request.plan().isBlank() ? "General enquiry" : request.plan().trim()) +
                "\n\nConsultation details:\n" + request.details().trim());
        try {
            mailSender.send(message);
        } catch (MailException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "The email provider rejected the message. Check the SMTP username and app password.");
        }
        return Map.of("message", "Your enquiry was sent successfully.");
    }

    public record ContactRequest(
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Size(max = 40) String telephone,
            @NotBlank @Email @Size(max = 190) String email,
            @NotBlank @Size(max = 30) String preference,
            @Size(max = 120) String plan,
            @NotBlank @Size(max = 3000) String details) { }
}
