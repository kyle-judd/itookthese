package io.itookthese.api.service;

import io.itookthese.api.dto.ContactSubmissionRequest;
import io.itookthese.api.dto.ContactSubmissionResponse;
import io.itookthese.api.entity.ContactSubmission;
import io.itookthese.api.repository.ContactSubmissionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ContactSubmissionService {
  private final ContactSubmissionRepository contactSubmissionRepository;
  private final EmailNotificationService emailNotificationService;

  @Transactional
  public void submitContact(ContactSubmissionRequest submissionRequest) {
    if (submissionRequest.honeypot() != null && !submissionRequest.honeypot().isBlank()) {
      return;
    }
    contactSubmissionRepository.save(mapContactSubmissionRequest(submissionRequest));
    emailNotificationService.sendContactNotification(submissionRequest);
  }

  public List<ContactSubmissionResponse> getAllContactSubmissions() {
    return contactSubmissionRepository.findAll().stream()
        .map(this::mapContactSubmissionToResponse)
        .toList();
  }

  @Transactional
  public ContactSubmissionResponse markContactAsRead(Long id) {
    ContactSubmission submission =
        contactSubmissionRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    submission.setIsRead(true);
    return mapContactSubmissionToResponse(contactSubmissionRepository.save(submission));
  }

  private ContactSubmission mapContactSubmissionRequest(
      ContactSubmissionRequest submissionRequest) {
    return ContactSubmission.builder()
        .name(submissionRequest.name())
        .email(submissionRequest.email())
        .subject(submissionRequest.subject())
        .message(submissionRequest.message())
        .isRead(false)
        .build();
  }

  private ContactSubmissionResponse mapContactSubmissionToResponse(
      ContactSubmission contactSubmission) {
    return new ContactSubmissionResponse(
        contactSubmission.getId(),
        contactSubmission.getName(),
        contactSubmission.getEmail(),
        contactSubmission.getSubject(),
        contactSubmission.getMessage(),
        contactSubmission.getIsRead(),
        contactSubmission.getCreatedAt());
  }
}
