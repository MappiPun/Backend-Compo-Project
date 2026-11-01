package se331.project2backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se331.project2backend.dto.QuestionDTO;
import se331.project2backend.dto.QuestionRequest;
import se331.project2backend.entity.Level;
import se331.project2backend.entity.Question;
import se331.project2backend.mapper.DtoMapper;
import se331.project2backend.repository.LevelRepository;
import se331.project2backend.repository.QuestionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final LevelRepository levelRepository;
    private final DtoMapper mapper;

    /** @param level when null, the questions of every level are returned */
    @Transactional(readOnly = true)
    public List<QuestionDTO> findAll(Integer level) {
        List<Question> questions = level == null
                ? questionRepository.findAllByOrderByLevel_LevelNumberAscIdAsc()
                : questionRepository.findByLevel_LevelNumberOrderByIdAsc(level);
        return questions.stream().map(mapper::toQuestionDTO).toList();
    }

    @Transactional(readOnly = true)
    public QuestionDTO findById(Long id) {
        return mapper.toQuestionDTO(find(id));
    }

    @Transactional
    public QuestionDTO create(QuestionRequest req) {
        Question question = new Question();
        apply(question, req);
        return mapper.toQuestionDTO(questionRepository.save(question));
    }

    @Transactional
    public QuestionDTO update(Long id, QuestionRequest req) {
        Question question = find(id);
        apply(question, req);
        return mapper.toQuestionDTO(questionRepository.save(question));
    }

    @Transactional
    public void delete(Long id) {
        questionRepository.delete(find(id));
    }

    private void apply(Question question, QuestionRequest req) {
        if (req.correctAnswer() >= req.options().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The correct answer must be one of the choices");
        }
        Level level = levelRepository.findByLevelNumber(req.level())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Level " + req.level() + " does not exist"));
        question.setLevel(level);
        question.setQuestionText(req.questionText().trim());
        question.setOptions(new ArrayList<>(req.options().stream().map(String::trim).toList()));
        question.setCorrectAnswer(req.correctAnswer());
    }

    private Question find(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question " + id + " was not found"));
    }
}
