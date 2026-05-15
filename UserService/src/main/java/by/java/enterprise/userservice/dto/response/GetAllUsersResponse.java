package by.java.enterprise.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

import java.util.List;

public record GetAllUsersResponse(
        @JsonProperty("users")
        @Nullable
        List<UserResponse> users,

        @JsonProperty("errorMessage")
        @Nullable
        String errorMessage
) {
}
