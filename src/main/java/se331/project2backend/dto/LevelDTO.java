package se331.project2backend.dto;

import java.util.List;

public record LevelDTO(
        Long id,
        int level,
        String title,
        String description,
        int passScore,
        int questionCount,
        List<BenefitDTO> benefits,
        List<ServiceDTO> services) {
}
