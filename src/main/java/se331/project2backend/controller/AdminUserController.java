package se331.project2backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se331.project2backend.dto.ExamHistoryDTO;
import se331.project2backend.dto.StatusRequest;
import se331.project2backend.dto.UserDTO;
import se331.project2backend.dto.UserUpdateRequest;
import se331.project2backend.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * The user list. page starts at 1. The total number of the users is sent in the x-total-count header.
     * Filters (all optional and can be combined): q = name or surname, level, province.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> list(@RequestParam(required = false) String q,
                                              @RequestParam(required = false) Integer level,
                                              @RequestParam(required = false) String province,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        Page<UserDTO> result = userService.search(q, level, province, page, size);
        return ResponseEntity.ok()
                .header("x-total-count", String.valueOf(result.getTotalElements()))
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "x-total-count")
                .body(result.getContent());
    }

    @GetMapping("/provinces")
    public List<String> provinces() {
        return userService.provinces();
    }

    @GetMapping("/users/{id}")
    public UserDTO get(@PathVariable Long id) {
        return userService.get(id);
    }

    @PutMapping("/users/{id}")
    public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateByAdmin(id, request);
    }

    @PatchMapping("/users/{id}/status")
    public UserDTO setStatus(@PathVariable Long id, @RequestBody StatusRequest request, Authentication authentication) {
        return userService.setActive(id, request.active(), authentication.getName());
    }

    @GetMapping("/users/{id}/exam-history")
    public List<ExamHistoryDTO> examHistory(@PathVariable Long id) {
        return userService.examHistory(id);
    }
}
