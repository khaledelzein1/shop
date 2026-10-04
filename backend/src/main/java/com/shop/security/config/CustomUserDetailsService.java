package com.shop.security.config;

import com.shop.user.User;
import com.shop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * {@code login} est un email (sujet des JWT, ou saisi au login) ou un nom d'utilisateur — voir
   * {@link UserRepository#findByLogin}.
   */
  @Override
  public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByLogin(login)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
    return new UserPrincipal(user);
  }
}
