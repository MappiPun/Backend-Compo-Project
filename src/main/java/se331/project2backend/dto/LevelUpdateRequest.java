package se331.project2backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record LevelUpdateRequest(
        @NotBlank(message = "Title is required") @Size(max = 255) String title,
        @Size(max = 1000) String description,
        @Min(value = 1, message = "Pass score must be at least 1") int passScore,
        @NotNull List<@Valid BenefitRequest> benefits,
        @NotNull List<@Valid ServiceRequest> services) {

    public record BenefitRequest(@NotBlank(message = "Benefit description is required") @Size(max = 500) String description) {
    }

    public record ServiceRequest(
            @NotBlank(message = "Service name is required") @Size(max = 255) String name,
            @Size(max = 255) String badge,
            @Size(max = 1000) String description) {
    }
}
