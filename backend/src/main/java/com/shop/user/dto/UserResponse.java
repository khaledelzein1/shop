package com.shop.user.dto;

import com.shop.user.User;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        Set<String> roles) {

    public static UserResponse from(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), roleNames);
    }
}
