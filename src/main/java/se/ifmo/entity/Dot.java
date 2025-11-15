package se.ifmo.entity;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.ifmo.api.request.HitRequest;
import se.ifmo.api.response.HitResponse;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Dot {
    @Id
    @GeneratedValue
    private Long id;

    @Expose
    private Double x;
    @Expose
    private Double y;
    @Expose
    private Double r;
    @Expose
    private Boolean hit;
    @Expose
    private Long execution_time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_dot_user_id")
    )
    private User owner;

    public Dot(HitRequest req, HitResponse resp, User owner) {
        x = req.getX();
        y = req.getY();
        r = req.getR();

        hit = resp.getHit();
        execution_time = resp.getExecutionTime();
        this.owner = owner;
    }
}
