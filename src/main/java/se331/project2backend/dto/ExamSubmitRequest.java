package se331.project2backend.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record ExamSubmitRequest(@NotEmpty(message = "No answers were sent") List<AnswerDTO> answers) {
}
