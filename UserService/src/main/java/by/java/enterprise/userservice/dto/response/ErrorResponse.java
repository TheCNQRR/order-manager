package by.java.enterprise.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorResponse(
        @JsonProperty("error")
        String errorMessage
) {
}
