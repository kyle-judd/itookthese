package io.itookthese.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.itookthese.api.config.GlobalExceptionHandler;
import io.itookthese.api.dto.ContactSubmissionRequest;
import io.itookthese.api.service.ContactSubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ContactSubmissionControllerTest {

  @Mock private ContactSubmissionService contactSubmissionService;
  @InjectMocks private ContactSubmissionController contactSubmissionController;
  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(contactSubmissionController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void submit_withValidData_returns201() throws Exception {
    ContactSubmissionRequest request =
        new ContactSubmissionRequest(
            "Jane Doe", "jane@example.com", "Hello there", "I love your photos! Great work.", null);
    doNothing().when(contactSubmissionService).submitContact(any());

    mockMvc
        .perform(
            post("/api/v1/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  void submit_withBlankName_returns400() throws Exception {
    ContactSubmissionRequest request =
        new ContactSubmissionRequest(
            "", "jane@example.com", "Hello there", "I love your photos! Great work.", null);

    mockMvc
        .perform(
            post("/api/v1/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void submit_withInvalidEmail_returns400() throws Exception {
    ContactSubmissionRequest request =
        new ContactSubmissionRequest(
            "Jane Doe", "not-an-email", "Hello there", "I love your photos! Great work.", null);

    mockMvc
        .perform(
            post("/api/v1/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void submit_withShortMessage_returns400() throws Exception {
    ContactSubmissionRequest request =
        new ContactSubmissionRequest(
            "Jane Doe", "jane@example.com", "Hello there", "Short", null);

    mockMvc
        .perform(
            post("/api/v1/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
