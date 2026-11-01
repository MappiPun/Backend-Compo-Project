package se331.project2backend.dto;

public record ExamInfoDTO(
        int currentLevel,
        int maxLevel,
        int examLevel,
        int passScore,
        int questionCount,
        boolean canTakeExam,
        String message) {
}
