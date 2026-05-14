package by.java.enterprise.userservice.kafka;

import by.java.enterprise.userservice.entity.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRegisterEvent(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String phone,
        UserRole role,
        LocalDateTime registeredAt
) {
}
