package com.shop.user;

import com.shop.common.dto.PageResponse;
import com.shop.common.exception.ConflictException;
import com.shop.common.exception.ResourceNotFoundException;
import com.shop.user.dto.AdminUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponse> search(String q, Pageable pageable) {
        Page<User> page = (q == null || q.isBlank())
                ? userRepository.findAll(pageable)
                : userRepository.findByEmailContainingIgnoreCase(q, pageable);
        return PageResponse.from(page.map(AdminUserResponse::from));
    }

    @Transactional(readOnly = true)
    public AdminUserResponse getById(Long id) {
        return AdminUserResponse.from(findByIdOrThrow(id));
    }

    @Transactional
    public AdminUserResponse updateStatus(Long id, boolean enabled, Long currentAdminId) {
        if (id.equals(currentAdminId) && !enabled) {
            throw new ConflictException("Vous ne pouvez pas désactiver votre propre compte");
        }
        User user = findByIdOrThrow(id);
        user.setEnabled(enabled);
        return AdminUserResponse.from(user);
    }

    private User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable (id=" + id + ")"));
    }
}
