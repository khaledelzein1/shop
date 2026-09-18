package com.shop.address;

import com.shop.address.dto.AddressRequest;
import com.shop.address.dto.AddressResponse;
import com.shop.security.config.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Carnet d'adresses de l'utilisateur connecté — pas de rôle spécifique requis, juste être authentifié. */
@RestController
@RequestMapping("/api/me/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public List<AddressResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
        return addressService.list(principal.getId());
    }

    @PostMapping
    public ResponseEntity<AddressResponse> create(
            @AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(principal.getId(), request));
    }

    @PutMapping("/{id}")
    public AddressResponse update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        return addressService.update(principal.getId(), id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        addressService.delete(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
