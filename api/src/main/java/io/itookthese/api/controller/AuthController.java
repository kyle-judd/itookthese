package io.itookthese.api.controller;

import io.itookthese.api.dto.LoginRequest;
import io.itookthese.api.dto.LoginResponse;
import io.itookthese.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final JwtTokenProvider jwtTokenProvider;

  @Value("${admin.username}")
  private String username;

  @Value("${admin.password}")
  private String password;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    if (!username.equals(loginRequest.username())
        || !password.equals(loginRequest.password())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    String token = jwtTokenProvider.generateToken(loginRequest.username());
    return ResponseEntity.ok(new LoginResponse(token));
  }
}
