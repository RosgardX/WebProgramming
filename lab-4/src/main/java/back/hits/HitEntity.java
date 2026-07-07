package back.hits;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "hits")
public class HitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double x;

    @Column(nullable = false)
    private double y;

    @Column(nullable = false)
    private double r;

    @Column(nullable = false)
    private boolean hit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "user_id", nullable = false)
    private Long userId; // FK на пользователя (храним id)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getR() { return r; }
    public void setR(double r) { this.r = r; }

    public boolean isHit() { return hit; }
    public void setHit(boolean hit) { this.hit = hit; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}