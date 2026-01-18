package pse.nebula.user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "blacklisted_tokens", indexes = {
    @Index(name = "idx_token", columnList = "token"),
    @Index(name = "idx_expiry", columnList = "expiresAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime blacklistedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public BlacklistedToken(String token, LocalDateTime expiresAt) {
        this.token = token;
        this.blacklistedAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }
}
