package se331.project2backend.dto;

import java.util.List;

public record QuestionDTO(
        Long id,
        int level,
        String questionText,
        List<String> options,
        int correctAnswer) {
}
