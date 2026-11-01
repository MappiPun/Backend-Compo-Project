package se331.project2backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String questionText;

    /** the choices of the multiple choice question */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_option", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "option_order")
    @Column(name = "option_text", nullable = false, length = 500)
    @Builder.Default
    private List<String> options = new ArrayList<>();

    /** index of the correct choice in {@link #options} */
    @Column(nullable = false)
    private int correctAnswer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "level_id")
    private Level level;
}
