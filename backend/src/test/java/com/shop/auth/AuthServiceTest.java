package com.shop.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.shop.auth.dto.AuthResponse;
import com.shop.auth.dto.LoginRequest;
import com.shop.auth.dto.RegisterRequest;
import com.shop.common.exception.EmailAlreadyUsedException;
import com.shop.security.jwt.JwtProperties;
import com.shop.security.jwt.JwtService;
import com.shop.user.Role;
import com.shop.user.RoleName;
import com.shop.user.RoleRepository;
import com.shop.user.User;
import com.shop.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_withExistingEmail_throwsEmailAlreadyUsedException() {
        RegisterRequest request = new RegisterRequest("taken@example.com", "password123", "A", "B");
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request)).isInstanceOf(EmailAlreadyUsedException.class);
    }

    @Test
    void register_withNewEmail_hashesPasswordAndAssignsUserRole() {
        RegisterRequest request = new RegisterRequest("new@example.com", "password123", "A", "B");
        Role userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", 42L);
            return user;
        });
        when(jwtProperties.getExpirationMs()).thenReturn(86_400_000L);
        when(jwtService.generateToken(any())).thenReturn("fake-jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.accessToken()).isEqualTo("fake-jwt-token");
        assertThat(response.user().email()).isEqualTo("new@example.com");
        assertThat(response.user().roles()).containsExactly("ROLE_USER");
        assertThat(response.expiresInSeconds()).isEqualTo(86_400L);
    }

    @Test
    void login_withBadCredentials_propagatesException() {
        LoginRequest request = new LoginRequest("user@example.com", "wrong-password");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad creds"));

        assertThatThrownBy(() -> authService.login(request)).isInstanceOf(BadCredentialsException.class);
    }
}
