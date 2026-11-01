package se331.project2backend.dto;

import java.util.List;

/** a question sent to the user who takes the exam - it never contains the correct answer */
public record ExamQuestionDTO(Long id, String questionText, List<String> options) {
}
