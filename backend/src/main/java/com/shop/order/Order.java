package com.shop.order;

import com.shop.common.domain.BaseEntity;
import com.shop.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

/**
 * Une commande est un document figé : les infos produit (dans {@link OrderItem}) et l'adresse de
 * livraison sont recopiées au moment du checkout plutôt que référencées en direct, pour rester
 * exactes même si le catalogue ou le carnet d'adresses changent ensuite (voir
 * docs/DOMAIN_MODEL.md).
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private OrderStatus status;

  @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @BatchSize(size = 20)
  private List<OrderItem> items = new ArrayList<>();

  // Snapshot de l'adresse de livraison au moment du checkout
  @Column(name = "shipping_label")
  private String shippingLabel;

  @Column(name = "shipping_street", nullable = false)
  private String shippingStreet;

  @Column(name = "shipping_city", nullable = false)
  private String shippingCity;

  @Column(name = "shipping_zip_code", nullable = false)
  private String shippingZipCode;

  @Column(name = "shipping_country", nullable = false)
  private String shippingCountry;

  /**
   * Session Stripe Checkout ouverte pour payer cette commande. Le statut de paiement est toujours
   * relu chez Stripe à partir de cet id, jamais déduit de ce que renvoie le navigateur.
   */
  @Column(name = "stripe_session_id", unique = true)
  private String stripeSessionId;

  /** Carte utilisée ("Visa", "Mastercard"…) — renseigné par le formulaire de carte intégré. */
  @Column(name = "card_brand", length = 30)
  private String cardBrand;

  /** 4 derniers chiffres de la carte ; le numéro complet n'est jamais stocké. */
  @Column(name = "card_last4", length = 4)
  private String cardLast4;
}
