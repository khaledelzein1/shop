package com.shop.auth;

import com.shop.auth.dto.AuthResponse;
import com.shop.auth.dto.LoginRequest;
import com.shop.auth.dto.RegisterRequest;
import com.shop.common.exception.EmailAlreadyUsedException;
import com.shop.security.config.UserPrincipal;
import com.shop.security.jwt.JwtProperties;
import com.shop.security.jwt.JwtService;
import com.shop.user.Role;
import com.shop.user.RoleName;
import com.shop.user.RoleRepository;
import com.shop.user.User;
import com.shop.user.UserRepository;
import com.shop.user.dto.UserResponse;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final JwtProperties jwtProperties;

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new EmailAlreadyUsedException(request.email());
    }

    Role userRole =
        roleRepository
            .findByName(RoleName.ROLE_USER)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Rôle ROLE_USER manquant en base — la migration de seed a-t-elle bien tourné ?"));

    User user = new User();
    user.setEmail(request.email());
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());
    user.setRoles(Set.of(userRole));
    userRepository.save(user);

    return buildAuthResponse(user);
  }

  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(
                () -> new IllegalStateException("Utilisateur authentifié introuvable en base"));

    return buildAuthResponse(user);
  }

  private AuthResponse buildAuthResponse(User user) {
    String token = jwtService.generateToken(new UserPrincipal(user));
    return new AuthResponse(
        token, "Bearer", jwtProperties.getExpirationMs() / 1000, UserResponse.from(user));
  }
}
