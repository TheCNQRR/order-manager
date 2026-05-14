package by.java.enterprise.userservice.dto.response;

import by.java.enterprise.userservice.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("email")
        String email,

        @JsonProperty("firstName")
        String firstName,

        @JsonProperty("lastName")
        String lastName,

        @JsonProperty("phone")
        String phone,

        @JsonProperty("role")
        UserRole role,

        @JsonProperty("registeredAt")
        LocalDateTime registeredAt
) {
}
