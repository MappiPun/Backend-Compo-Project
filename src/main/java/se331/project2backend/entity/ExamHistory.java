package se331.project2backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "exam_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    /** the level of the exam which the user took */
    @Column(name = "exam_level", nullable = false)
    private int level;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private boolean passed;

    /** the level (badge) which the user received, null when the user failed */
    private Integer awardedLevel;

    @Column(nullable = false)
    private Instant attemptedAt;
}
