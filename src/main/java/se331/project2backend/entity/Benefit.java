package se331.project2backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "benefit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Benefit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "level_id")
    private Level level;
}
