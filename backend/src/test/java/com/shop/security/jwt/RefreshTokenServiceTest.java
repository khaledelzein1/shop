package com.shop.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.shop.user.User;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

  @Mock private RefreshTokenRepository refreshTokenRepository;
  @Mock private JwtProperties jwtProperties;

  @InjectMocks private RefreshTokenService refreshTokenService;

  @Test
  void issue_savesHashedTokenAndReturnsRawToken() {
    when(jwtProperties.getRefreshExpirationMs()).thenReturn(604_800_000L);
    User user = new User();

    String rawToken = refreshTokenService.issue(user);

    assertThat(rawToken).isNotBlank();
    org.mockito.ArgumentCaptor<RefreshToken> captor =
        org.mockito.ArgumentCaptor.forClass(RefreshToken.class);
    org.mockito.Mockito.verify(refreshTokenRepository).save(captor.capture());
    assertThat(captor.getValue().getTokenHash()).isNotEqualTo(rawToken);
    assertThat(captor.getValue().getTokenHash()).hasSize(64); // hex SHA-256
    assertThat(captor.getValue().isRevoked()).isFalse();
  }

  @Test
  void consume_withUnknownToken_throwsCredentialsExpiredException() {
    when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> refreshTokenService.consume("unknown-token"))
        .isInstanceOf(CredentialsExpiredException.class);
  }

  @Test
  void consume_withExpiredToken_throwsCredentialsExpiredException() {
    RefreshToken expired = new RefreshToken();
    expired.setExpiresAt(Instant.now().minusSeconds(60));
    expired.setRevoked(false);
    when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(expired));

    assertThatThrownBy(() -> refreshTokenService.consume("expired-token"))
        .isInstanceOf(CredentialsExpiredException.class);
  }

  @Test
  void consume_withValidToken_revokesItAndReturnsUser() {
    User user = new User();
    ReflectionTestUtils.setField(user, "id", 7L);

    RefreshToken valid = new RefreshToken();
    valid.setExpiresAt(Instant.now().plusSeconds(3600));
    valid.setRevoked(false);
    valid.setUser(user);
    when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(valid));

    User result = refreshTokenService.consume("valid-token");

    assertThat(result).isSameAs(user);
    assertThat(valid.isRevoked()).isTrue();
  }
}
