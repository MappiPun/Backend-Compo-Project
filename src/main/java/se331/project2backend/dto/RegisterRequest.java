package se331.project2backend.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 30, message = "Username must be 3-30 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and _")
        String username,
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
        String password,
        @NotBlank(message = "Name is required") @Size(max = 100) String name,
        @NotBlank(message = "Surname is required") @Size(max = 100) String surname,
        @NotBlank(message = "Email is required") @Email(message = "Email is not valid") String email,
        @Pattern(regexp = "^$|^[0-9+\\- ]{9,15}$", message = "Phone number is not valid") String phone,
        @NotBlank(message = "Province is required") String province,
        @Size(max = 1000) String profileImage) {
}
