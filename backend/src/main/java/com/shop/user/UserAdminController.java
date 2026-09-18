package com.shop.user;

import com.shop.common.dto.PageResponse;
import com.shop.security.config.UserPrincipal;
import com.shop.user.dto.AdminUserResponse;
import com.shop.user.dto.UpdateUserStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserAdminController {

  private final UserAdminService userAdminService;

  @GetMapping
  public PageResponse<AdminUserResponse> search(
      @RequestParam(required = false) String q,
      @PageableDefault(size = 20, sort = "email") Pageable pageable) {
    return userAdminService.search(q, pageable);
  }

  @GetMapping("/{id}")
  public AdminUserResponse getById(@PathVariable Long id) {
    return userAdminService.getById(id);
  }

  @PatchMapping("/{id}/status")
  public AdminUserResponse updateStatus(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long id,
      @Valid @RequestBody UpdateUserStatusRequest request) {
    return userAdminService.updateStatus(id, request.enabled(), principal.getId());
  }
}
