package se331.project2backend.mapper;

import org.springframework.stereotype.Component;
import se331.project2backend.dto.*;
import se331.project2backend.entity.*;

import java.util.List;

@Component
public class DtoMapper {

    public UserDTO toUserDTO(AppUser u) {
        return new UserDTO(
                u.getId(),
                u.getUsername(),
                u.getName(),
                u.getSurname(),
                u.getEmail(),
                u.getPhone(),
                u.getProvince(),
                u.getRole().name().toLowerCase(),
                u.getLevel(),
                u.isActive(),
                u.getProfileImage());
    }

    public List<UserDTO> toUserDTOs(List<AppUser> users) {
        return users.stream().map(this::toUserDTO).toList();
    }

    public LevelDTO toLevelDTO(Level l, int questionCount) {
        return new LevelDTO(
                l.getId(),
                l.getLevelNumber(),
                l.getTitle(),
                l.getDescription(),
                l.getPassScore(),
                questionCount,
                l.getBenefits().stream().map(b -> new BenefitDTO(b.getId(), b.getDescription())).toList(),
                l.getServices().stream()
                        .map(s -> new ServiceDTO(s.getId(), s.getName(), s.getBadge(), s.getDescription()))
                        .toList());
    }

    public QuestionDTO toQuestionDTO(Question q) {
        return new QuestionDTO(
                q.getId(),
                q.getLevel().getLevelNumber(),
                q.getQuestionText(),
                List.copyOf(q.getOptions()),
                q.getCorrectAnswer());
    }

    public ExamQuestionDTO toExamQuestionDTO(Question q) {
        return new ExamQuestionDTO(q.getId(), q.getQuestionText(), List.copyOf(q.getOptions()));
    }

    public ELearningDTO toELearningDTO(ELearningContent c) {
        return new ELearningDTO(
                c.getId(),
                c.getLevel().getLevelNumber(),
                c.getTitle(),
                c.getDescription(),
                c.getVideoUrl());
    }

    public ExamHistoryDTO toExamHistoryDTO(ExamHistory h) {
        return new ExamHistoryDTO(
                h.getId(),
                h.getLevel(),
                h.getScore(),
                h.getTotalQuestions(),
                h.isPassed(),
                h.getAttemptedAt().toString(),
                h.getAwardedLevel());
    }
}
