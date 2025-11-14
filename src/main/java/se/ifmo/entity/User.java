package se.ifmo.entity;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.util.Base64;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Expose
    @Column(nullable = false)
    private String login;
    @Expose
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String hash;

    @PrePersist
    protected void onCreate() {
        if (hash == null || hash.isEmpty()) {
            this.hash = generateSalt();
            this.password = DigestUtils.sha256Hex(password + hash);
        }
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
