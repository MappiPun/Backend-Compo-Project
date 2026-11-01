package se331.project2backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se331.project2backend.dto.AuthResponse;
import se331.project2backend.dto.LoginRequest;
import se331.project2backend.dto.RegisterRequest;
import se331.project2backend.dto.UserDTO;
import se331.project2backend.entity.AppUser;
import se331.project2backend.entity.Role;
import se331.project2backend.mapper.DtoMapper;
import se331.project2backend.repository.UserRepository;
import se331.project2backend.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DtoMapper mapper;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsernameIgnoreCase(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This username is already taken");
        }
        if (userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already registered");
        }
        String image = req.profileImage() == null || req.profileImage().isBlank()
                ? "https://api.dicebear.com/7.x/bottts/svg?seed=" + req.username()
                : req.profileImage();

        // every new user starts at Level 1 and is active, so they can log in right away
        AppUser user = AppUser.builder()
                .username(req.username().trim())
                .password(passwordEncoder.encode(req.password()))
                .name(req.name().trim())
                .surname(req.surname().trim())
                .email(req.email().trim())
                .phone(req.phone())
                .province(req.province())
                .profileImage(image)
                .role(Role.USER)
                .level(1)
                .active(true)
                .build();
        user = userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user.getUsername(), user.getRole().name()),
                mapper.toUserDTO(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        AppUser user = userRepository.findByUsername(req.username().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This account has been deactivated. Please contact the administrator.");
        }
        return new AuthResponse(jwtService.generateToken(user.getUsername(), user.getRole().name()),
                mapper.toUserDTO(user));
    }

    @Transactional(readOnly = true)
    public UserDTO me(String username) {
        return mapper.toUserDTO(userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")));
    }
}
