package by.java.enterprise.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

public record LoginResponse(
        @JsonProperty("token")
        @Nullable
        String token,

        @JsonProperty("errorMessage")
        @Nullable
        String message
) {
}
