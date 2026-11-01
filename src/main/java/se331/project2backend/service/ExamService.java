package se331.project2backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se331.project2backend.dto.*;
import se331.project2backend.entity.AppUser;
import se331.project2backend.entity.ExamHistory;
import se331.project2backend.entity.Level;
import se331.project2backend.entity.Question;
import se331.project2backend.mapper.DtoMapper;
import se331.project2backend.repository.ExamHistoryRepository;
import se331.project2backend.repository.LevelRepository;
import se331.project2backend.repository.QuestionRepository;
import se331.project2backend.repository.UserRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The upgrade exam. The exam of the level N is taken by the users who are at level N,
 * and when they pass it they are upgraded to level N+1 immediately.
 * The answers are graded in the backend, so the correct answers never leave the server.
 */
@Service
@RequiredArgsConstructor
public class ExamService {

    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final QuestionRepository questionRepository;
    private final ExamHistoryRepository examHistoryRepository;
    private final DtoMapper mapper;

    @Transactional(readOnly = true)
    public ExamInfoDTO info(String username) {
        AppUser user = findUser(username);
        int max = levelRepository.findMaxLevelNumber();
        int level = user.getLevel();
        if (level >= max) {
            return new ExamInfoDTO(level, max, level, 0, 0, false, "You have reached the highest level. Congratulations!");
        }
        Level examLevel = findLevel(level);
        int count = (int) questionRepository.countByLevel_LevelNumber(level);
        if (count == 0) {
            return new ExamInfoDTO(level, max, level, examLevel.getPassScore(), 0, false,
                    "There are no questions for this level yet.");
        }
        return new ExamInfoDTO(level, max, level, examLevel.getPassScore(), count, true, null);
    }

    /** The questions of the current level of the user, without the correct answers. */
    @Transactional(readOnly = true)
    public List<ExamQuestionDTO> questions(String username) {
        AppUser user = findUser(username);
        ensureCanTakeExam(user);
        return questionRepository.findByLevel_LevelNumberOrderByIdAsc(user.getLevel())
                .stream().map(mapper::toExamQuestionDTO).toList();
    }

    @Transactional
    public ExamResultDTO submit(String username, ExamSubmitRequest req) {
        AppUser user = findUser(username);
        ensureCanTakeExam(user);

        int examLevel = user.getLevel();
        List<Question> questions = questionRepository.findByLevel_LevelNumberOrderByIdAsc(examLevel);
        if (questions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There are no questions for this level yet");
        }

        Map<Long, Integer> answers = new HashMap<>();
        for (AnswerDTO a : req.answers()) {
            answers.put(a.questionId(), a.selected());
        }
        // the exam cannot be submitted before it is completed
        for (Question q : questions) {
            Integer selected = answers.get(q.getId());
            if (selected == null || selected < 0 || selected >= q.getOptions().size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please answer every question before submitting");
            }
        }

        int score = 0;
        for (Question q : questions) {
            if (answers.get(q.getId()) == q.getCorrectAnswer()) {
                score++;
            }
        }
        int passScore = findLevel(examLevel).getPassScore();
        boolean passed = score >= passScore;

        int previousLevel = user.getLevel();
        if (passed) {
            user.setLevel(examLevel + 1); // upgraded immediately
            userRepository.save(user);
        }

        ExamHistory history = examHistoryRepository.save(ExamHistory.builder()
                .user(user)
                .level(examLevel)
                .score(score)
                .totalQuestions(questions.size())
                .passed(passed)
                .awardedLevel(passed ? examLevel + 1 : null)
                .attemptedAt(Instant.now())
                .build());

        return new ExamResultDTO(score, questions.size(), passScore, passed, previousLevel, user.getLevel(),
                mapper.toExamHistoryDTO(history), mapper.toUserDTO(user));
    }

    private void ensureCanTakeExam(AppUser user) {
        if (user.getLevel() >= levelRepository.findMaxLevelNumber()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already reached the highest level");
        }
    }

    private AppUser findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Level findLevel(int number) {
        return levelRepository.findByLevelNumber(number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Level " + number + " was not found"));
    }
}
