package se331.project2backend.dto;

import jakarta.validation.constraints.*;

public record UserUpdateRequest(
        @NotBlank(message = "Name is required") @Size(max = 100) String name,
        @NotBlank(message = "Surname is required") @Size(max = 100) String surname,
        @NotBlank(message = "Email is required") @Email(message = "Email is not valid") String email,
        @Pattern(regexp = "^$|^[0-9+\\- ]{9,15}$", message = "Phone number is not valid") String phone,
        @NotBlank(message = "Province is required") String province,
        @Size(max = 1000) String profileImage,
        Integer level) {
}
