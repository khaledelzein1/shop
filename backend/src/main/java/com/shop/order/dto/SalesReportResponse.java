package com.shop.order.dto;

import com.shop.order.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Tableau de bord des ventes sur les {@code days} derniers jours. Seules les commandes payées
 * comptent (CONFIRMED, SHIPPED, DELIVERED) ; {@code previous*} couvre la période de même durée
 * juste avant, pour afficher une évolution.
 */
public record SalesReportResponse(
    int days,
    BigDecimal revenue,
    long orderCount,
    BigDecimal averageOrderValue,
    long itemsSold,
    BigDecimal previousRevenue,
    long previousOrderCount,
    List<DailySales> daily,
    List<TopProduct> topProducts,
    List<RecentSale> recentSales) {

  public record DailySales(LocalDate date, BigDecimal revenue, long orders) {}

  /** {@code name} : le modèle (ex. « LONG PUFFER COAT »), à défaut le nom du produit. */
  public record TopProduct(String name, long quantity, BigDecimal revenue) {}

  public record RecentSale(
      Long id, Instant createdAt, String customerEmail, BigDecimal total, OrderStatus status) {}
}
