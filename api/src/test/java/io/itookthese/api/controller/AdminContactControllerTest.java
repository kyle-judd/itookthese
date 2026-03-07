package io.itookthese.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.itookthese.api.config.GlobalExceptionHandler;
import io.itookthese.api.dto.ContactSubmissionResponse;
import io.itookthese.api.service.ContactSubmissionService;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AdminContactControllerTest {

  @Mock private ContactSubmissionService contactSubmissionService;
  @InjectMocks private AdminContactController adminContactController;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(adminContactController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  private ContactSubmissionResponse sampleSubmission() {
    return new ContactSubmissionResponse(
        1L, "John Doe", "Hello", "john@example.com", "Great portfolio!", false,
        OffsetDateTime.now());
  }

  @Test
  void getAllSubmissions_returnsList() throws Exception {
    when(contactSubmissionService.getAllContactSubmissions())
        .thenReturn(List.of(sampleSubmission()));

    mockMvc
        .perform(get("/api/v1/admin/contact-submissions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("John Doe"))
        .andExpect(jsonPath("$[0].email").value("john@example.com"));
  }

  @Test
  void markAsRead_returns200() throws Exception {
    ContactSubmissionResponse read =
        new ContactSubmissionResponse(
            1L, "John Doe", "Hello", "john@example.com", "Great portfolio!", true,
            OffsetDateTime.now());
    when(contactSubmissionService.markContactAsRead(1L)).thenReturn(read);

    mockMvc
        .perform(patch("/api/v1/admin/contact-submissions/1/read"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isRead").value(true));
  }
}
