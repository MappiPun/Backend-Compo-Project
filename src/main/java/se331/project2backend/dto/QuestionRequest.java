package se331.project2backend.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record QuestionRequest(
        @Min(value = 1, message = "Level is required") int level,
        @NotBlank(message = "Question text is required") @Size(max = 1000) String questionText,
        @NotNull @Size(min = 2, max = 6, message = "A question needs 2-6 choices") List<@NotBlank(message = "A choice cannot be empty") @Size(max = 500) String> options,
        @Min(value = 0, message = "Select the correct answer") int correctAnswer) {
}
