package se331.project2backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se331.project2backend.dto.ELearningDTO;
import se331.project2backend.dto.ELearningRequest;
import se331.project2backend.service.ELearningService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/elearning")
@RequiredArgsConstructor
public class AdminELearningController {

    private final ELearningService eLearningService;

    @GetMapping
    public List<ELearningDTO> list(@RequestParam(required = false) Integer level) {
        return eLearningService.findAll(level);
    }

    @PostMapping
    public ResponseEntity<ELearningDTO> create(@Valid @RequestBody ELearningRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eLearningService.create(request));
    }

    @PutMapping("/{id}")
    public ELearningDTO update(@PathVariable Long id, @Valid @RequestBody ELearningRequest request) {
        return eLearningService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eLearningService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
