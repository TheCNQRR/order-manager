package by.java.enterprise.userservice.dto.response;

import by.java.enterprise.userservice.entity.AuthStatus;
import jakarta.annotation.Nullable;

public record AuthResult(
        AuthStatus status,

        @Nullable
        String token,

        @Nullable
        String errorMessage
) {
}
