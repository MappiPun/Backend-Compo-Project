package se331.project2backend.dto;

public record ExamHistoryDTO(
        Long id,
        int level,
        int score,
        int totalQuestions,
        boolean passed,
        String attemptedAt,
        Integer awardedLevel) {
}
