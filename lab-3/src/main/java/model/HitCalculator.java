package model;

import jakarta.inject.Named;
import jakarta.enterprise.context.ApplicationScoped;

@Named("hitCalculator")
@ApplicationScoped
public class HitCalculator {

    public boolean isHit(double x, double y, double r) {
        return inQuarterCircle(x, y, r) || inRectangle(x, y, r) || inTriangle(x, y, r);
    }

    /** Проверка четверти круга (первая четверть). */
    private boolean inQuarterCircle(double x, double y, double r) {
        if (x < 0 || y < 0) {
            return false;
        }
        return x * x + y * y <= r * r;
    }

    /** Проверка прямоугольника в третьей четверти. */
    private boolean inRectangle(double x, double y, double r) {
        return (x >= -r / 2.0 && x <= 0.0) && (y >= -r && y <= 0.0);
    }

    /** Проверка треугольника в четвёртой четверти. */
    private boolean inTriangle(double x, double y, double r) {
        if (x < 0.0 || x > r / 2.0) return false;
        if (y < -r || y > 0.0) return false;
        if (y < 2*(x-(r/2))) return false;
        return y <= 2.0 * x;
    }
}