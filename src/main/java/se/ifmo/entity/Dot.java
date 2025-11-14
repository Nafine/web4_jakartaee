package se.ifmo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Dot {
    @Id
    @GeneratedValue
    private Long id;

    private Double x;
    private Double y;
    private Double r;
    private Boolean hit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "fk_dot_user_id")
    )
    private User user;
}
