package se331.project2backend.dto;

import jakarta.validation.constraints.*;

public record ELearningRequest(
        @Min(value = 1, message = "Level is required") int level,
        @NotBlank(message = "Title is required") @Size(max = 255) String title,
        @Size(max = 1000) String description,
        @NotBlank(message = "Video url is required") @Size(max = 500) String videoUrl) {
}
