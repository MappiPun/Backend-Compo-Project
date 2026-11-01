package se331.project2backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "level")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "level_number", nullable = false, unique = true)
    private int levelNumber;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    /** minimum number of correct answers needed to pass the exam of this level */
    @Column(nullable = false)
    @Builder.Default
    private int passScore = 1;

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @Builder.Default
    private List<Benefit> benefits = new ArrayList<>();

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @Builder.Default
    private List<ServiceItem> services = new ArrayList<>();

    public void addBenefit(Benefit b) {
        b.setLevel(this);
        benefits.add(b);
    }

    public void addService(ServiceItem s) {
        s.setLevel(this);
        services.add(s);
    }
}
