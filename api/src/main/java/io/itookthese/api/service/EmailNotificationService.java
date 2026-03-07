package io.itookthese.api.service;

import io.itookthese.api.dto.ContactSubmissionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificationService {

  private final JavaMailSender mailSender;

  @Value("${mail.notification.to:}")
  private String notificationTo;

  @Value("${mail.notification.enabled:false}")
  private boolean enabled;

  public EmailNotificationService(@Autowired(required = false) JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendContactNotification(ContactSubmissionRequest request) {
    if (!enabled || mailSender == null || notificationTo.isBlank()) {
      return;
    }

    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(notificationTo);
      message.setSubject("New Contact: " + request.subject());
      message.setText(
          "Name: " + request.name() + "\n"
              + "Email: " + request.email() + "\n"
              + "Subject: " + request.subject() + "\n\n"
              + request.message());
      message.setReplyTo(request.email());
      mailSender.send(message);
    } catch (MailException e) {
      log.error("Failed to send contact notification email", e);
    }
  }
}
