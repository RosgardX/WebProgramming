package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PointResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private double x;
    private double y;
    private double r;
    private boolean hit;
    private LocalDateTime createdAt;

    public PointResult() {
    }

    public PointResult(double x, double y, double r, boolean hit, LocalDateTime createdAt) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.createdAt = createdAt;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PointResult{" +
                "x=" + x +
                ", y=" + y +
                ", r=" + r +
                ", hit=" + hit +
                ", createdAt=" + createdAt +
                '}';
    }
}
