package back.hits;

import org.springframework.stereotype.Component;

@Component
public class HitChecker {

    public boolean isHit(double x, double y, double r) {
        if (r <= 0) return false;

        // 1) Четверть круга в 4 квадранте
        if (x >= 0 && y <= 0 && (x * x + y * y <= (r * r)/4)) {
            return true;
        }

        // 2) Прямоугольник в 3
        if (x >= -r  && x <= 0 && y >= -r/2 && y <= 0) {
            return true;
        }

        // 3) Треугольник во 2
        if (x <= 0 && x >= -(r / 2) && y <= r/2 && y >= 0 && y <= x + (r / 2)) {
            return true;
        }

        return false;
    }
}