package se331.project2backend.dto;

public record ExamResultDTO(
        int score,
        int totalQuestions,
        int passScore,
        boolean passed,
        int previousLevel,
        int newLevel,
        ExamHistoryDTO attempt,
        UserDTO user) {
}
