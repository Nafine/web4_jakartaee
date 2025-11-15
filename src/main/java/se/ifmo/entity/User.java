package se.ifmo.entity;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.util.Base64;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @XmlElement(required = true)
    @Column(nullable = false)
    private String name;
    @XmlElement(required = true)
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
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
