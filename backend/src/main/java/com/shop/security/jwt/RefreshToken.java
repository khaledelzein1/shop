package com.shop.security.jwt;

import com.shop.common.domain.BaseEntity;
import com.shop.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Jamais le token brut n'est persisté — uniquement son empreinte SHA-256 ({@link #tokenHash}).
 * Rotation : un refresh consomme (révoque) l'ancien et en émet un nouveau, ce qui permet de
 * détecter un vol (si un token révoqué est rejoué, c'est le signe qu'il a fuité).
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken extends BaseEntity {

  @Column(name = "token_hash", nullable = false, unique = true, length = 64)
  private String tokenHash;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(nullable = false)
  private boolean revoked = false;

  public boolean isValid() {
    return !revoked && expiresAt.isAfter(Instant.now());
  }
}
