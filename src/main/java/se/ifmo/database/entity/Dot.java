package se.ifmo.database.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
public class Dot {
    @Id
    @GeneratedValue
    private Long id;

    private Double x;
    private Double y;
    private Double r;
    private Boolean result;
    private Instant timestamp;
    private Long executionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
}
