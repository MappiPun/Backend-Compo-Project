package se331.project2backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se331.project2backend.entity.Question;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByLevel_LevelNumberOrderByIdAsc(int levelNumber);

    List<Question> findAllByOrderByLevel_LevelNumberAscIdAsc();

    long countByLevel_LevelNumber(int levelNumber);
}
