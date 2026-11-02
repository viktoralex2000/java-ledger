package io.github.viktoralex2000.ledger.dto;

import io.github.viktoralex2000.ledger.model.UserEntity;

import java.time.Instant;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String role,
        String status,
        Instant createdAt
) {
    public static UserDto from(UserEntity user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole() != null ? user.getRole().name() : null,
                        user.getStatus() != null ? user.getStatus().name() : null,
                user.getCreatedAt()
        );
    }
}
