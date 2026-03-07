package io.itookthese.api.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Jwts;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenProviderTest {

  private JwtTokenProvider jwtTokenProvider;

  private static final String SECRET = "test-secret-key-that-is-long-enough-for-hmac-sha256";
  private static final long EXPIRATION = 3600000L;

  @BeforeEach
  void setUp() {
    jwtTokenProvider = new JwtTokenProvider();
    ReflectionTestUtils.setField(jwtTokenProvider, "secret", SECRET);
    ReflectionTestUtils.setField(jwtTokenProvider, "expiration", EXPIRATION);
  }

  @Test
  void generateToken_returnsNonNullToken() {
    String token = jwtTokenProvider.generateToken("admin");
    assertThat(token).isNotNull().isNotBlank();
  }

  @Test
  void getUsernameFromToken_returnsCorrectUsername() {
    String token = jwtTokenProvider.generateToken("admin");
    String username = jwtTokenProvider.getUsernameFromToken(token);
    assertThat(username).isEqualTo("admin");
  }

  @Test
  void validateToken_withValidToken_returnsTrue() {
    String token = jwtTokenProvider.generateToken("admin");
    assertThat(jwtTokenProvider.validateToken(token)).isTrue();
  }

  @Test
  void validateToken_withExpiredToken_returnsFalse() {
    SecretKey key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
    String expiredToken =
        Jwts.builder()
            .subject("admin")
            .issuedAt(new Date(System.currentTimeMillis() - 7200000))
            .expiration(new Date(System.currentTimeMillis() - 3600000))
            .signWith(key)
            .compact();
    assertThat(jwtTokenProvider.validateToken(expiredToken)).isFalse();
  }

  @Test
  void validateToken_withMalformedToken_returnsFalse() {
    assertThat(jwtTokenProvider.validateToken("not-a-valid-jwt-token")).isFalse();
  }

  @Test
  void validateToken_withWrongSigningKey_returnsFalse() {
    SecretKey wrongKey =
        new SecretKeySpec(
            "wrong-secret-key-that-is-long-enough-for-hmac-sha256".getBytes(), "HmacSHA256");
    String token =
        Jwts.builder()
            .subject("admin")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
            .signWith(wrongKey)
            .compact();
    assertThat(jwtTokenProvider.validateToken(token)).isFalse();
  }

  @Test
  void getUsernameFromToken_withDifferentUsernames() {
    String token1 = jwtTokenProvider.generateToken("user1");
    String token2 = jwtTokenProvider.generateToken("user2");
    assertThat(jwtTokenProvider.getUsernameFromToken(token1)).isEqualTo("user1");
    assertThat(jwtTokenProvider.getUsernameFromToken(token2)).isEqualTo("user2");
  }

  @Test
  void getUsernameFromToken_withInvalidToken_throwsException() {
    assertThatThrownBy(() -> jwtTokenProvider.getUsernameFromToken("invalid-token"))
        .isInstanceOf(Exception.class);
  }
}
