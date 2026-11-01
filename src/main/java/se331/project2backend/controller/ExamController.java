package se331.project2backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se331.project2backend.dto.ExamInfoDTO;
import se331.project2backend.dto.ExamQuestionDTO;
import se331.project2backend.dto.ExamResultDTO;
import se331.project2backend.dto.ExamSubmitRequest;
import se331.project2backend.service.ExamService;

import java.util.List;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping("/info")
    public ExamInfoDTO info(Authentication authentication) {
        return examService.info(authentication.getName());
    }

    @GetMapping("/questions")
    public List<ExamQuestionDTO> questions(Authentication authentication) {
        return examService.questions(authentication.getName());
    }

    @PostMapping("/submit")
    public ExamResultDTO submit(Authentication authentication, @Valid @RequestBody ExamSubmitRequest request) {
        return examService.submit(authentication.getName(), request);
    }
}
