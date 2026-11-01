package se331.project2backend.dto;

public record AuthResponse(String jwtToken, UserDTO user) {
}
