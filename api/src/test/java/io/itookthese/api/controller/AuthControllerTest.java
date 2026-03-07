package io.itookthese.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.itookthese.api.config.GlobalExceptionHandler;
import io.itookthese.api.dto.LoginRequest;
import io.itookthese.api.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock private JwtTokenProvider jwtTokenProvider;
  private AuthController authController;
  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String ADMIN_USERNAME = "testadmin";
  private static final String ADMIN_PASSWORD = "password123";

  @BeforeEach
  void setUp() {
    authController = new AuthController(jwtTokenProvider);
    ReflectionTestUtils.setField(authController, "username", ADMIN_USERNAME);
    ReflectionTestUtils.setField(authController, "password", ADMIN_PASSWORD);
    mockMvc =
        MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void login_withValidCredentials_returnsToken() throws Exception {
    when(jwtTokenProvider.generateToken("testadmin")).thenReturn("mock-jwt-token");

    LoginRequest request = new LoginRequest("testadmin", "password123");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("mock-jwt-token"));
  }

  @Test
  void login_withInvalidUsername_returnsUnauthorized() throws Exception {
    LoginRequest request = new LoginRequest("wronguser", "password123");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void login_withInvalidPassword_returnsUnauthorized() throws Exception {
    LoginRequest request = new LoginRequest("testadmin", "wrongpassword");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }
}
