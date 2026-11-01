package se331.project2backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se331.project2backend.dto.LevelDTO;
import se331.project2backend.dto.LevelUpdateRequest;
import se331.project2backend.entity.Benefit;
import se331.project2backend.entity.Level;
import se331.project2backend.entity.ServiceItem;
import se331.project2backend.mapper.DtoMapper;
import se331.project2backend.repository.LevelRepository;
import se331.project2backend.repository.QuestionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepository;
    private final QuestionRepository questionRepository;
    private final DtoMapper mapper;

    @Transactional(readOnly = true)
    public List<LevelDTO> findAll() {
        return levelRepository.findAllByOrderByLevelNumberAsc().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public LevelDTO findByNumber(int levelNumber) {
        return toDto(find(levelNumber));
    }

    /** Admin: edit the title, the pass criteria, the benefits and the services of a level. */
    @Transactional
    public LevelDTO update(int levelNumber, LevelUpdateRequest req) {
        Level level = find(levelNumber);
        level.setTitle(req.title().trim());
        level.setDescription(req.description());
        level.setPassScore(req.passScore());

        level.getBenefits().clear();
        req.benefits().forEach(b -> level.addBenefit(Benefit.builder().description(b.description().trim()).build()));

        level.getServices().clear();
        req.services().forEach(s -> level.addService(ServiceItem.builder()
                .name(s.name().trim())
                .badge(s.badge())
                .description(s.description())
                .build()));

        return toDto(levelRepository.save(level));
    }

    private Level find(int levelNumber) {
        return levelRepository.findByLevelNumber(levelNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Level " + levelNumber + " was not found"));
    }

    private LevelDTO toDto(Level level) {
        return mapper.toLevelDTO(level, (int) questionRepository.countByLevel_LevelNumber(level.getLevelNumber()));
    }
}
