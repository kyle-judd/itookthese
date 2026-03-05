package io.itookthese.api.service;

import io.itookthese.api.dto.ContactSubmissionRequest;
import io.itookthese.api.entity.ContactSubmission;
import io.itookthese.api.repository.ContactSubmissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ContactSubmissionService {
  private final ContactSubmissionRepository contactSubmissionRepository;

  @Transactional
  public void submitContact(ContactSubmissionRequest submissionRequest) {
    if (submissionRequest.honeypot() != null && !submissionRequest.honeypot().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    ContactSubmission contactSubmission = createContactSubmissionFromRequest(submissionRequest);
    contactSubmission.setIsRead(false);
    contactSubmissionRepository.save(contactSubmission);
  }

  private ContactSubmission createContactSubmissionFromRequest(
      ContactSubmissionRequest submissionRequest) {
    ContactSubmission contactSubmission = new ContactSubmission();
    contactSubmission.setName(submissionRequest.name());
    contactSubmission.setEmail(submissionRequest.email());
    contactSubmission.setSubject(submissionRequest.subject());
    contactSubmission.setMessage(submissionRequest.message());
    return contactSubmission;
  }
}
