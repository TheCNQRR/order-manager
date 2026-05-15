package by.java.enterprise.userservice.dto.request;

import by.java.enterprise.userservice.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

public record UpdateUserRequest(
        @JsonProperty("email")
        @Nullable
        @Email(message = "email must be valid")
        @Size(max = 255, message = "email length must not exceed 255 characters")
        String email,

        @JsonProperty("oldPassword")
        @Nullable
        String oldPassword,

        @JsonProperty("newPassword")
        @Nullable
        String newPassword,

        @JsonProperty("firstName")
        @Nullable
        @Size(min = 2, max = 32, message = "first name length must be between 2 and 32 characters long")
        String firstName,

        @JsonProperty("lastName")
        @Nullable
        @Size(min = 2, max = 32, message = "last name length must be between 2 and 32 characters long")
        String lastName,

        @JsonProperty("phone")
        @Nullable
        @Pattern(regexp = "^\\+7\\d{10}$", message = "Номер должен быть в формате +7XXXXXXXXXX")
        String phone,

        @JsonProperty("role")
        @Nullable
        UserRole role
) {
}
