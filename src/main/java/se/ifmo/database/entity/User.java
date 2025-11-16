package se.ifmo.database.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.eclipse.persistence.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
@ToString
public class User {
    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator(name = "uuid")
    @Column(name = "uuid")
    private UUID uuid;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String passwordHash;
    @Column(nullable = false)
    private String salt;
}
