package se.ifmo.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.ifmo.api.hit.HitRequest;
import se.ifmo.api.hit.HitResponse;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Dot {
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private Double x;
    private Double y;
    private Double r;
    private Boolean hit;
    private Long execution_time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_dot_user_id")
    )
    @JsonIgnore
    private User owner;

    public Dot(HitRequest req, HitResponse resp, User owner) {
        x = req.x();
        y = req.y();
        r = req.r();

        hit = resp.hit();
        execution_time = resp.executionTime();
        this.owner = owner;
    }
}
