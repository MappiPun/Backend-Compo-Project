package se331.project2backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se331.project2backend.dto.AuthResponse;
import se331.project2backend.dto.LoginRequest;
import se331.project2backend.dto.RegisterRequest;
import se331.project2backend.dto.UserDTO;
import se331.project2backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /** The logged in user (used to restore the login after the page is reloaded). */
    @GetMapping("/me")
    public UserDTO me(Authentication authentication) {
        return authService.me(authentication.getName());
    }
}
