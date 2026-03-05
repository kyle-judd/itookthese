package io.itookthese.api.service;

import io.itookthese.api.dto.ContactSubmissionRequest;
import io.itookthese.api.entity.ContactSubmission;
import io.itookthese.api.repository.ContactSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    ContactSubmission contactSubmission = mapContactSubmissionRequest(submissionRequest);
    contactSubmission.setIsRead(false);
    contactSubmissionRepository.save(contactSubmission);
  }

  private ContactSubmission mapContactSubmissionRequest(
      ContactSubmissionRequest submissionRequest) {
    ContactSubmission contactSubmission = new ContactSubmission();
    contactSubmission.setName(submissionRequest.name());
    contactSubmission.setEmail(submissionRequest.email());
    contactSubmission.setSubject(submissionRequest.subject());
    contactSubmission.setMessage(submissionRequest.message());
    return contactSubmission;
  }
}
