package com.shop.order;

import com.shop.order.dto.SalesReportResponse;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository
    extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

  Page<Order> findByUserId(Long userId, Pageable pageable);

  Optional<Order> findByIdAndUserId(Long id, Long userId);

  /** Commandes PENDING avec un paiement Stripe ouvert avant {@code cutoff}. */
  @Query(
      "select o.id from Order o where o.status = :status and o.stripeSessionId is not null"
          + " and o.createdAt < :cutoff")
  List<Long> findIdsAwaitingPayment(
      @Param("status") OrderStatus status, @Param("cutoff") Instant cutoff);

  // ---- Rapport des ventes (admin) -----------------------------------------------------------

  /** Date et montant des commandes dans {@code [from, to)} — agrégées par jour côté service. */
  @Query(
      "select o.createdAt, o.totalAmount from Order o"
          + " where o.status in :statuses and o.createdAt >= :from and o.createdAt < :to")
  List<Object[]> findSaleAmounts(
      @Param("statuses") Collection<OrderStatus> statuses,
      @Param("from") Instant from,
      @Param("to") Instant to);

  /**
   * Quantités et CA par modèle vendu (attribut {@code model} de la variante ; nom du produit si la
   * variante a été supprimée depuis), du plus vendu au moins vendu.
   */
  @Query(
      nativeQuery = true,
      value =
          "select coalesce(v.attributes ->> 'model', oi.product_name) as name,"
              + " sum(oi.quantity) as quantity, sum(oi.quantity * oi.unit_price) as revenue"
              + " from order_items oi"
              + " join orders o on o.id = oi.order_id"
              + " left join product_variants v on v.id = oi.variant_id"
              + " where o.status in (:statuses) and o.created_at >= :from"
              + " group by 1 order by 2 desc, 3 desc")
  List<Object[]> findSalesByModel(
      @Param("statuses") Collection<String> statuses, @Param("from") Instant from);

  @Query(
      "select new com.shop.order.dto.SalesReportResponse$RecentSale("
          + "o.id, o.createdAt, u.email, o.totalAmount, o.status)"
          + " from Order o join o.user u where o.status in :statuses order by o.createdAt desc")
  List<SalesReportResponse.RecentSale> findRecentSales(
      @Param("statuses") Collection<OrderStatus> statuses, Pageable pageable);
}
