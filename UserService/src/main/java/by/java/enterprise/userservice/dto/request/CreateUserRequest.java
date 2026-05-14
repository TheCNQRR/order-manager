package by.java.enterprise.userservice.dto.request;

import by.java.enterprise.userservice.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public record CreateUserRequest(
        @JsonProperty("email")
        @NotBlank(message = "email must be not null")
        @Email(message = "email must be valid")
        @Size(max = 255, message = "email length must not exceed 255 characters")
        String email,

        @JsonProperty("password")
        @NotBlank
        String password,

        @JsonProperty("firstName")
        @NotBlank(message = "first name must be not null")
        @Size(min = 2, max = 32, message = "first name length must be between 2 and 32 characters long")
        String firstName,

        @JsonProperty("lastName")
        @NotBlank(message = "last name must be not null")
        @Size(min = 2, max = 32, message = "last name length must be between 2 and 32 characters long")
        String lastName,

        @JsonProperty("phone")
        @NotBlank(message = "phone must be not null")
        @Pattern(regexp = "^\\+7\\d{10}$", message = "Номер должен быть в формате +7XXXXXXXXXX")
        String phone,

        @JsonProperty("role")
        @NotNull(message = "role must be not null")
        UserRole role
) {
}
