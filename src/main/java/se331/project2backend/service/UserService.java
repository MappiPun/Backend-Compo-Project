package se331.project2backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se331.project2backend.dto.ExamHistoryDTO;
import se331.project2backend.dto.UserDTO;
import se331.project2backend.dto.UserUpdateRequest;
import se331.project2backend.entity.AppUser;
import se331.project2backend.entity.Role;
import se331.project2backend.mapper.DtoMapper;
import se331.project2backend.repository.ExamHistoryRepository;
import se331.project2backend.repository.LevelRepository;
import se331.project2backend.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ExamHistoryRepository examHistoryRepository;
    private final LevelRepository levelRepository;
    private final DtoMapper mapper;

    /** Admin: the user list with the pagination and the filters (name/surname, level, province). page is 1-based. */
    @Transactional(readOnly = true)
    public Page<UserDTO> search(String q, Integer level, String province, int page, int size) {
        String keyword = q == null ? "" : q.trim();
        String prov = province == null ? "" : province.trim();
        int minLevel = level == null ? 1 : level;
        int maxLevel = level == null ? Integer.MAX_VALUE : level;
        PageRequest pageable = PageRequest.of(Math.max(page, 1) - 1, Math.min(Math.max(size, 1), 100), Sort.by("id"));
        return userRepository.search(Role.USER, keyword, minLevel, maxLevel, prov, pageable).map(mapper::toUserDTO);
    }

    @Transactional(readOnly = true)
    public List<String> provinces() {
        return userRepository.findDistinctProvinces(Role.USER);
    }

    @Transactional(readOnly = true)
    public UserDTO get(Long id) {
        return mapper.toUserDTO(find(id));
    }

    @Transactional(readOnly = true)
    public List<ExamHistoryDTO> examHistory(Long userId) {
        find(userId);
        return examHistoryRepository.findByUser_IdOrderByAttemptedAtDesc(userId)
                .stream().map(mapper::toExamHistoryDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ExamHistoryDTO> examHistoryOf(String username) {
        return examHistory(findByUsername(username).getId());
    }

    /** Admin: edit the details of a user (the level can be changed too). */
    @Transactional
    public UserDTO updateByAdmin(Long id, UserUpdateRequest req) {
        AppUser user = find(id);
        apply(user, req);
        if (req.level() != null) {
            int max = levelRepository.findMaxLevelNumber();
            if (req.level() < 1 || req.level() > max) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Level must be between 1 and " + max);
            }
            user.setLevel(req.level());
        }
        return mapper.toUserDTO(userRepository.save(user));
    }

    /** User: edit my own details (the level and the role can never be changed here). */
    @Transactional
    public UserDTO updateSelf(String username, UserUpdateRequest req) {
        AppUser user = findByUsername(username);
        apply(user, req);
        return mapper.toUserDTO(userRepository.save(user));
    }

    /** Admin: deactivate / reactivate a user. */
    @Transactional
    public UserDTO setActive(Long id, boolean active, String adminUsername) {
        AppUser user = find(id);
        if (user.getUsername().equals(adminUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot deactivate your own account");
        }
        if (user.getRole() == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An admin account cannot be deactivated");
        }
        user.setActive(active);
        return mapper.toUserDTO(userRepository.save(user));
    }

    private void apply(AppUser user, UserUpdateRequest req) {
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(req.email(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already registered");
        }
        user.setName(req.name().trim());
        user.setSurname(req.surname().trim());
        user.setEmail(req.email().trim());
        user.setPhone(req.phone());
        user.setProvince(req.province());
        if (req.profileImage() != null && !req.profileImage().isBlank()) {
            user.setProfileImage(req.profileImage());
        }
    }

    private AppUser find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + id + " was not found"));
    }

    private AppUser findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
