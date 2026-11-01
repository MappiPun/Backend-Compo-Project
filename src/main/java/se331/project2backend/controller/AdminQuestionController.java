package se331.project2backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se331.project2backend.dto.QuestionDTO;
import se331.project2backend.dto.QuestionRequest;
import se331.project2backend.service.QuestionService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final QuestionService questionService;

    @GetMapping
    public List<QuestionDTO> list(@RequestParam(required = false) Integer level) {
        return questionService.findAll(level);
    }

    @GetMapping("/{id}")
    public QuestionDTO get(@PathVariable Long id) {
        return questionService.findById(id);
    }

    @PostMapping
    public ResponseEntity<QuestionDTO> create(@Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.create(request));
    }

    @PutMapping("/{id}")
    public QuestionDTO update(@PathVariable Long id, @Valid @RequestBody QuestionRequest request) {
        return questionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
