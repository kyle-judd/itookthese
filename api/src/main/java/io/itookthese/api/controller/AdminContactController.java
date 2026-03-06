package io.itookthese.api.controller;

import io.itookthese.api.dto.ContactSubmissionResponse;
import io.itookthese.api.service.ContactSubmissionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/contact-submissions")
@RequiredArgsConstructor
public class AdminContactController {
  private final ContactSubmissionService contactSubmissionService;

  @GetMapping
  public ResponseEntity<List<ContactSubmissionResponse>> getAllContactSubmissions() {
    List<ContactSubmissionResponse> responses = contactSubmissionService.getAllContactSubmissions();
    return ResponseEntity.ok(responses);
  }

  @PatchMapping("/{id}/read")
  public ResponseEntity<ContactSubmissionResponse> markContactAsRead(@PathVariable Long id) {
    ContactSubmissionResponse markContactAsReadResponse =
        contactSubmissionService.markContactAsRead(id);
    return ResponseEntity.status(HttpStatus.OK).body(markContactAsReadResponse);
  }
}
