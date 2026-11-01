package se331.project2backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se331.project2backend.dto.ELearningDTO;
import se331.project2backend.service.ELearningService;

import java.util.List;

@RestController
@RequestMapping("/api/elearning")
@RequiredArgsConstructor
public class ELearningController {

    private final ELearningService eLearningService;

    /** The eLearning content which matches the current level of the user. */
    @GetMapping
    public List<ELearningDTO> mine(Authentication authentication) {
        return eLearningService.findForUser(authentication.getName());
    }
}
