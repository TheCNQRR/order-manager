package by.java.enterprise.userservice.kafka;

import java.util.UUID;

public record DeleteUserEvent(
        UUID id
) {
}
