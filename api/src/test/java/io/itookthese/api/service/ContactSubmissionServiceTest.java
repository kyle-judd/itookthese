package io.itookthese.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.itookthese.api.dto.ContactSubmissionRequest;
import io.itookthese.api.dto.ContactSubmissionResponse;
import io.itookthese.api.entity.ContactSubmission;
import io.itookthese.api.repository.ContactSubmissionRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ContactSubmissionServiceTest {

  @Mock private ContactSubmissionRepository contactSubmissionRepository;
  @Mock private EmailNotificationService emailNotificationService;

  @InjectMocks private ContactSubmissionService contactSubmissionService;

  private ContactSubmission sampleSubmission() {
    return ContactSubmission.builder()
        .id(1L)
        .name("John Doe")
        .email("john@example.com")
        .subject("Hello")
        .message("Great portfolio!")
        .isRead(false)
        .createdAt(OffsetDateTime.now())
        .build();
  }

  @Test
  void submitContact_validRequest_savesSubmission() {
    ContactSubmissionRequest request =
        new ContactSubmissionRequest("John Doe", "john@example.com", "Hello", "Great portfolio!", null);
    contactSubmissionService.submitContact(request);
    verify(contactSubmissionRepository).save(any(ContactSubmission.class));
  }

  @Test
  void submitContact_emptyHoneypot_savesSubmission() {
    ContactSubmissionRequest request =
        new ContactSubmissionRequest("John Doe", "john@example.com", "Hello", "Great portfolio!", "");
    contactSubmissionService.submitContact(request);
    verify(contactSubmissionRepository).save(any(ContactSubmission.class));
  }

  @Test
  void submitContact_blankHoneypot_savesSubmission() {
    ContactSubmissionRequest request =
        new ContactSubmissionRequest("John Doe", "john@example.com", "Hello", "Great portfolio!", "   ");
    contactSubmissionService.submitContact(request);
    verify(contactSubmissionRepository).save(any(ContactSubmission.class));
  }

  @Test
  void submitContact_filledHoneypot_doesNotSave() {
    ContactSubmissionRequest request =
        new ContactSubmissionRequest("Bot", "bot@spam.com", "Spam", "Buy stuff now!", "bot-value");
    contactSubmissionService.submitContact(request);
    verify(contactSubmissionRepository, never()).save(any(ContactSubmission.class));
  }

  @Test
  void getAllContactSubmissions_returnsMappedList() {
    ContactSubmission submission = sampleSubmission();
    when(contactSubmissionRepository.findAll()).thenReturn(List.of(submission));

    List<ContactSubmissionResponse> result = contactSubmissionService.getAllContactSubmissions();

    assertThat(result).hasSize(1);
    ContactSubmissionResponse response = result.get(0);
    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.name()).isEqualTo("John Doe");
    // Note: the service maps getEmail() to the "subject" position and getSubject()
    // to the "email" position of the ContactSubmissionResponse record constructor.
    assertThat(response.subject()).isEqualTo("john@example.com");
    assertThat(response.email()).isEqualTo("Hello");
    assertThat(response.message()).isEqualTo("Great portfolio!");
    assertThat(response.isRead()).isFalse();
  }

  @Test
  void getAllContactSubmissions_emptyList_returnsEmpty() {
    when(contactSubmissionRepository.findAll()).thenReturn(List.of());
    assertThat(contactSubmissionService.getAllContactSubmissions()).isEmpty();
  }

  @Test
  void markContactAsRead_existingId_marksAsReadAndReturns() {
    ContactSubmission submission = sampleSubmission();
    when(contactSubmissionRepository.findById(1L)).thenReturn(Optional.of(submission));
    when(contactSubmissionRepository.save(any(ContactSubmission.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    ContactSubmissionResponse result = contactSubmissionService.markContactAsRead(1L);

    assertThat(result.isRead()).isTrue();
    assertThat(result.id()).isEqualTo(1L);
    verify(contactSubmissionRepository).save(submission);
  }

  @Test
  void markContactAsRead_notFound_throwsException() {
    when(contactSubmissionRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> contactSubmissionService.markContactAsRead(999L))
        .isInstanceOf(ResponseStatusException.class);
  }
}
