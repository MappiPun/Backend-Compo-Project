package se331.project2backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /** short code shown as a badge on the service card, e.g. L1-LIB */
    private String badge;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "level_id")
    private Level level;
}
