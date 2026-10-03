package com.shop.security.jwt;

import com.shop.user.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Émission et rotation des refresh tokens : token opaque (256 bits aléatoires, pas un JWT — il n'a
 * pas besoin d'être auto-porteur), seule son empreinte SHA-256 est persistée.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtProperties jwtProperties;

  @Transactional
  public String issue(User user) {
    String rawToken = generateOpaqueToken();

    RefreshToken entity = new RefreshToken();
    entity.setTokenHash(hash(rawToken));
    entity.setUser(user);
    entity.setExpiresAt(Instant.now().plusMillis(jwtProperties.getRefreshExpirationMs()));
    entity.setRevoked(false);
    refreshTokenRepository.save(entity);

    return rawToken;
  }

  /**
   * Valide le token, le révoque (rotation — un refresh token ne se consomme qu'une fois) et renvoie
   * l'utilisateur associé.
   */
  @Transactional
  public User consume(String rawToken) {
    RefreshToken entity =
        refreshTokenRepository
            .findByTokenHash(hash(rawToken))
            .filter(RefreshToken::isValid)
            .orElseThrow(() -> new CredentialsExpiredException("Refresh token invalid or expired"));

    entity.setRevoked(true);
    return entity.getUser();
  }

  @Transactional
  public void revoke(String rawToken) {
    refreshTokenRepository.findByTokenHash(hash(rawToken)).ifPresent(rt -> rt.setRevoked(true));
  }

  private String generateOpaqueToken() {
    byte[] bytes = new byte[32];
    SECURE_RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private String hash(String rawToken) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hashed);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 unavailable on this JVM", e);
    }
  }
}
