package se331.project2backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se331.project2backend.entity.ExamHistory;

import java.util.List;

public interface ExamHistoryRepository extends JpaRepository<ExamHistory, Long> {
    List<ExamHistory> findByUser_IdOrderByAttemptedAtDesc(Long userId);
}
