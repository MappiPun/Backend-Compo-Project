package se331.project2backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se331.project2backend.entity.ELearningContent;

import java.util.List;

public interface ELearningRepository extends JpaRepository<ELearningContent, Long> {
    List<ELearningContent> findByLevel_LevelNumberOrderByIdAsc(int levelNumber);

    List<ELearningContent> findAllByOrderByLevel_LevelNumberAscIdAsc();
}
