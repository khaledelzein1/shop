package com.shop.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.auth.AuthService;
import com.shop.common.exception.ConflictException;
import com.shop.common.exception.InvalidCurrentPasswordException;
import com.shop.security.jwt.RefreshTokenService;
import com.shop.user.dto.UpdateAccountRequest;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private RefreshTokenService refreshTokenService;

  @Mock private AuthService authService;

  @InjectMocks private AccountService accountService;

  private User admin;

  @BeforeEach
  void setUp() {
    admin = new User();
    ReflectionTestUtils.setField(admin, "id", 1L);
    admin.setEmail("admin@shop.local");
    admin.setUsername("admin");
    admin.setPasswordHash("old-hash");
    when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
  }

  @Test
  void update_withWrongCurrentPassword_throwsAndChangesNothing() {
    when(passwordEncoder.matches("wrong", "old-hash")).thenReturn(false);

    assertThatThrownBy(
            () ->
                accountService.update(1L, new UpdateAccountRequest("wrong", "boss", "NewPass123!")))
        .isInstanceOf(InvalidCurrentPasswordException.class);

    assertThat(admin.getUsername()).isEqualTo("admin");
    assertThat(admin.getPasswordHash()).isEqualTo("old-hash");
    verify(refreshTokenService, never()).revokeAll(any());
  }

  @Test
  void update_withUsernameTakenByAnotherUser_throwsConflict() {
    when(passwordEncoder.matches("current", "old-hash")).thenReturn(true);
    when(userRepository.existsByUsernameIgnoreCaseAndIdNot("boss", 1L)).thenReturn(true);

    assertThatThrownBy(
            () -> accountService.update(1L, new UpdateAccountRequest("current", "boss", null)))
        .isInstanceOf(ConflictException.class);
  }

  @Test
  void update_usernameOnly_keepsPasswordAndSessions() {
    when(passwordEncoder.matches("current", "old-hash")).thenReturn(true);
    when(userRepository.existsByUsernameIgnoreCaseAndIdNot("boss", 1L)).thenReturn(false);

    accountService.update(1L, new UpdateAccountRequest("current", " boss ", null));

    assertThat(admin.getUsername()).isEqualTo("boss");
    assertThat(admin.getPasswordHash()).isEqualTo("old-hash");
    verify(refreshTokenService, never()).revokeAll(any());
    verify(authService).issueTokens(admin);
  }

  @Test
  void update_withNewPassword_hashesItAndRevokesOtherSessions() {
    when(passwordEncoder.matches("current", "old-hash")).thenReturn(true);
    when(userRepository.existsByUsernameIgnoreCaseAndIdNot("admin", 1L)).thenReturn(false);
    when(passwordEncoder.encode("NewPass123!")).thenReturn("new-hash");

    accountService.update(1L, new UpdateAccountRequest("current", "admin", "NewPass123!"));

    assertThat(admin.getPasswordHash()).isEqualTo("new-hash");
    verify(refreshTokenService).revokeAll(admin);
    verify(authService).issueTokens(admin);
  }
}
