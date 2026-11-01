package se331.project2backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "elearning_content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ELearningContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    /** embeddable video url, e.g. https://www.youtube.com/embed/xxxx */
    @Column(nullable = false, length = 500)
    private String videoUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "level_id")
    private Level level;
}
