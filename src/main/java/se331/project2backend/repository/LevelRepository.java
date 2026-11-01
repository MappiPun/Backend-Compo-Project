package se331.project2backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se331.project2backend.entity.Level;

import java.util.List;
import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {
    Optional<Level> findByLevelNumber(int levelNumber);

    List<Level> findAllByOrderByLevelNumberAsc();

    @Query("select coalesce(max(l.levelNumber), 1) from Level l")
    int findMaxLevelNumber();
}
