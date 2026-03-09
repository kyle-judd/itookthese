package io.itookthese.api.service;

import io.itookthese.api.dto.ContactSubmissionRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificationService {

  @Value("${mail.notification.enabled:false}")
  private boolean enabled;

  @Value("${mail.notification.to:}")
  private String notificationTo;

  @Value("${mail.notification.from:noreply@itookthese.app}")
  private String notificationFrom;

  @Value("${resend.api.key:}")
  private String resendApiKey;

  public void sendContactNotification(ContactSubmissionRequest request) {
    if (!enabled || resendApiKey.isBlank() || notificationTo.isBlank()) {
      return;
    }

    try {
      String body = """
          {
            "from": "%s",
            "to": ["%s"],
            "subject": "New Contact: %s",
            "reply_to": "%s",
            "text": "Name: %s\\nEmail: %s\\nSubject: %s\\n\\n%s"
          }
          """.formatted(
          escape(notificationFrom),
          escape(notificationTo),
          escape(request.subject()),
          escape(request.email()),
          escape(request.name()),
          escape(request.email()),
          escape(request.subject()),
          escape(request.message()));

      HttpRequest httpRequest = HttpRequest.newBuilder()
          .uri(URI.create("https://api.resend.com/emails"))
          .header("Authorization", "Bearer " + resendApiKey)
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(body))
          .build();

      HttpResponse<String> response = HttpClient.newHttpClient()
          .send(httpRequest, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() >= 400) {
        log.error("Resend API error ({}): {}", response.statusCode(), response.body());
      }
    } catch (Exception e) {
      log.error("Failed to send contact notification email", e);
    }
  }

  private String escape(String value) {
    if (value == null) return "";
    return value.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");
  }
}
