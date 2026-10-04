package com.shop.order;

import com.shop.order.dto.SalesReportResponse;
import com.shop.order.dto.SalesReportResponse.DailySales;
import com.shop.order.dto.SalesReportResponse.TopProduct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalesService {

  /** Une commande est une vente une fois payée ; PENDING (non payée) et CANCELLED sont exclues. */
  static final Set<OrderStatus> PAID_STATUSES =
      EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.SHIPPED, OrderStatus.DELIVERED);

  private static final int TOP_PRODUCTS = 5;
  private static final int RECENT_SALES = 8;

  private final OrderRepository orderRepository;
  private final Clock clock = Clock.systemUTC();

  /** Fuseau dans lequel une « journée » de ventes est découpée (les dates sont stockées en UTC). */
  @Value("${app.sales.zone:Europe/Paris}")
  private ZoneId zone;

  @Transactional(readOnly = true)
  public SalesReportResponse report(int days) {
    LocalDate today = LocalDate.now(clock.withZone(zone));
    LocalDate firstDay = today.minusDays(days - 1L);
    Instant from = firstDay.atStartOfDay(zone).toInstant();
    Instant to = today.plusDays(1).atStartOfDay(zone).toInstant();
    Instant previousFrom = firstDay.minusDays(days).atStartOfDay(zone).toInstant();

    // Une ligne par jour de la période, y compris les jours sans vente (courbe continue).
    Map<LocalDate, BigDecimal> revenueByDay = new LinkedHashMap<>();
    Map<LocalDate, Long> ordersByDay = new LinkedHashMap<>();
    for (LocalDate d = firstDay; !d.isAfter(today); d = d.plusDays(1)) {
      revenueByDay.put(d, BigDecimal.ZERO);
      ordersByDay.put(d, 0L);
    }

    BigDecimal revenue = BigDecimal.ZERO;
    long orderCount = 0;
    for (Object[] row : orderRepository.findSaleAmounts(PAID_STATUSES, from, to)) {
      LocalDate day = ((Instant) row[0]).atZone(zone).toLocalDate();
      BigDecimal amount = (BigDecimal) row[1];
      revenueByDay.merge(day, amount, BigDecimal::add);
      ordersByDay.merge(day, 1L, Long::sum);
      revenue = revenue.add(amount);
      orderCount++;
    }

    BigDecimal previousRevenue = BigDecimal.ZERO;
    long previousOrderCount = 0;
    for (Object[] row : orderRepository.findSaleAmounts(PAID_STATUSES, previousFrom, from)) {
      previousRevenue = previousRevenue.add((BigDecimal) row[1]);
      previousOrderCount++;
    }

    List<String> statusNames = PAID_STATUSES.stream().map(Enum::name).toList();
    List<TopProduct> byModel = new ArrayList<>();
    long itemsSold = 0;
    for (Object[] row : orderRepository.findSalesByModel(statusNames, from)) {
      long quantity = ((Number) row[1]).longValue();
      itemsSold += quantity;
      byModel.add(new TopProduct((String) row[0], quantity, toMoney(row[2])));
    }

    List<DailySales> daily =
        revenueByDay.entrySet().stream()
            .map(e -> new DailySales(e.getKey(), e.getValue(), ordersByDay.get(e.getKey())))
            .toList();

    BigDecimal average =
        orderCount == 0
            ? BigDecimal.ZERO
            : revenue.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP);

    return new SalesReportResponse(
        days,
        revenue,
        orderCount,
        average,
        itemsSold,
        previousRevenue,
        previousOrderCount,
        daily,
        byModel.stream().limit(TOP_PRODUCTS).toList(),
        orderRepository.findRecentSales(PAID_STATUSES, PageRequest.of(0, RECENT_SALES)));
  }

  private static BigDecimal toMoney(Object value) {
    BigDecimal amount = value instanceof BigDecimal bd ? bd : new BigDecimal(String.valueOf(value));
    return amount.setScale(2, RoundingMode.HALF_UP);
  }
}
