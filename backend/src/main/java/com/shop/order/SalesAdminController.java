package com.shop.order;

import com.shop.order.dto.SalesReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/sales")
@PreAuthorize("hasRole('ADMIN')")
@Validated
@RequiredArgsConstructor
@Tag(
    name = "Admin - Ventes",
    description = "Chiffre d'affaires et produits les plus vendus (ADMIN)")
public class SalesAdminController {

  private final SalesService salesService;

  @GetMapping
  @Operation(
      summary = "Rapport des ventes",
      description =
          "CA, nombre de commandes, panier moyen, articles vendus, ventes par jour, top modèles et "
              + "dernières ventes sur les `days` derniers jours (commandes payées uniquement).")
  public SalesReportResponse report(@RequestParam(defaultValue = "30") @Min(1) @Max(366) int days) {
    return salesService.report(days);
  }
}
