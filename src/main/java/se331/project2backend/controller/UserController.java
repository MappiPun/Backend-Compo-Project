package se331.project2backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se331.project2backend.dto.ExamHistoryDTO;
import se331.project2backend.dto.UserDTO;
import se331.project2backend.dto.UserUpdateRequest;
import se331.project2backend.service.AuthService;
import se331.project2backend.service.UserService;

import java.util.List;

/** The profile of the logged in user. */
@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    public UserDTO me(Authentication authentication) {
        return authService.me(authentication.getName());
    }

    @PutMapping
    public UserDTO update(Authentication authentication, @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateSelf(authentication.getName(), request);
    }

    @GetMapping("/exam-history")
    public List<ExamHistoryDTO> examHistory(Authentication authentication) {
        return userService.examHistoryOf(authentication.getName());
    }
}
