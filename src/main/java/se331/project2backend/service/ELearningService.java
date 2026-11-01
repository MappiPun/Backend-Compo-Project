package se331.project2backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se331.project2backend.dto.ELearningDTO;
import se331.project2backend.dto.ELearningRequest;
import se331.project2backend.entity.AppUser;
import se331.project2backend.entity.ELearningContent;
import se331.project2backend.entity.Level;
import se331.project2backend.mapper.DtoMapper;
import se331.project2backend.repository.ELearningRepository;
import se331.project2backend.repository.LevelRepository;
import se331.project2backend.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ELearningService {

    private final ELearningRepository eLearningRepository;
    private final LevelRepository levelRepository;
    private final UserRepository userRepository;
    private final DtoMapper mapper;

    /** User: only the content of the current level of the user. */
    @Transactional(readOnly = true)
    public List<ELearningDTO> findForUser(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return eLearningRepository.findByLevel_LevelNumberOrderByIdAsc(user.getLevel())
                .stream().map(mapper::toELearningDTO).toList();
    }

    /** Admin: all the content or the content of a level. */
    @Transactional(readOnly = true)
    public List<ELearningDTO> findAll(Integer level) {
        List<ELearningContent> list = level == null
                ? eLearningRepository.findAllByOrderByLevel_LevelNumberAscIdAsc()
                : eLearningRepository.findByLevel_LevelNumberOrderByIdAsc(level);
        return list.stream().map(mapper::toELearningDTO).toList();
    }

    @Transactional
    public ELearningDTO create(ELearningRequest req) {
        ELearningContent content = new ELearningContent();
        apply(content, req);
        return mapper.toELearningDTO(eLearningRepository.save(content));
    }

    @Transactional
    public ELearningDTO update(Long id, ELearningRequest req) {
        ELearningContent content = find(id);
        apply(content, req);
        return mapper.toELearningDTO(eLearningRepository.save(content));
    }

    @Transactional
    public void delete(Long id) {
        eLearningRepository.delete(find(id));
    }

    private void apply(ELearningContent content, ELearningRequest req) {
        Level level = levelRepository.findByLevelNumber(req.level())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Level " + req.level() + " does not exist"));
        content.setLevel(level);
        content.setTitle(req.title().trim());
        content.setDescription(req.description());
        content.setVideoUrl(req.videoUrl().trim());
    }

    private ELearningContent find(Long id) {
        return eLearningRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Content " + id + " was not found"));
    }
}
