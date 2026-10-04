package com.shop.user;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  Optional<User> findByUsernameIgnoreCase(String username);

  boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);

  Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

  /**
   * Résout l'identifiant saisi au login : un email s'il contient « @ » (un nom d'utilisateur ne
   * peut pas en contenir), sinon un nom d'utilisateur.
   */
  default Optional<User> findByLogin(String login) {
    return login.contains("@") ? findByEmail(login) : findByUsernameIgnoreCase(login);
  }
}
