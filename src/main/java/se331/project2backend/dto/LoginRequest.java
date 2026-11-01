package se331.project2backend.dto;

import jakarta.validation.constraints.*;

public record LoginRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Password is required") String password) {
}
