package se331.project2backend.dto;

public record UserDTO(
        Long id,
        String username,
        String name,
        String surname,
        String email,
        String phone,
        String province,
        String role,
        int level,
        boolean active,
        String profileImage) {
}
