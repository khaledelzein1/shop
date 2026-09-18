package com.shop.config;

import com.shop.user.Role;
import com.shop.user.RoleName;
import com.shop.user.RoleRepository;
import com.shop.user.User;
import com.shop.user.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crée un compte admin de démonstration au démarrage si aucun n'existe encore, pour pouvoir tester
 * l'espace admin sans manipulation SQL manuelle.
 *
 * <p>Compromis assumé pour un projet portfolio : en vraie production, on désactiverait ce seeder
 * ({@code app.seed.admin.enabled=false}) et on créerait le premier admin via une procédure
 * d'exploitation dédiée plutôt qu'un mot de passe par défaut au démarrage.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAccountInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.seed.admin.enabled:true}")
  private boolean enabled;

  @Value("${app.seed.admin.email:admin@shop.local}")
  private String adminEmail;

  @Value("${app.seed.admin.password:ChangeMe123!}")
  private String adminPassword;

  @Override
  public void run(String... args) {
    if (!enabled || userRepository.existsByEmail(adminEmail)) {
      return;
    }

    Role adminRole =
        roleRepository
            .findByName(RoleName.ROLE_ADMIN)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Rôle ROLE_ADMIN manquant en base — la migration de seed a-t-elle bien tourné ?"));

    User admin = new User();
    admin.setEmail(adminEmail);
    admin.setPasswordHash(passwordEncoder.encode(adminPassword));
    admin.setFirstName("Admin");
    admin.setLastName("Shop");
    admin.setRoles(Set.of(adminRole));
    userRepository.save(admin);

    log.warn(
        "Compte admin de démo créé : {} / (mot de passe défini via app.seed.admin.password) "
            + "— à désactiver ou changer en dehors du dev local.",
        adminEmail);
  }
}
