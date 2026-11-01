package se331.project2backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import se331.project2backend.dto.LevelDTO;
import se331.project2backend.dto.LevelUpdateRequest;
import se331.project2backend.service.LevelService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    /** The levels with their benefits and services - for every logged in user. */
    @GetMapping("/api/levels")
    public List<LevelDTO> findAll() {
        return levelService.findAll();
    }

    @GetMapping("/api/levels/{levelNumber}")
    public LevelDTO findOne(@PathVariable int levelNumber) {
        return levelService.findByNumber(levelNumber);
    }

    /** Admin only (see SecurityConfiguration). */
    @PutMapping("/api/admin/levels/{levelNumber}")
    public LevelDTO update(@PathVariable int levelNumber, @Valid @RequestBody LevelUpdateRequest request) {
        return levelService.update(levelNumber, request);
    }
}
