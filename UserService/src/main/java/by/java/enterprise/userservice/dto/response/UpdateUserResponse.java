package by.java.enterprise.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

public record UpdateUserResponse(
        @JsonProperty("user")
        @Nullable
        UserResponse user,

        @JsonProperty("errorMessage")
        @Nullable
        String errorMessage
) {
}
