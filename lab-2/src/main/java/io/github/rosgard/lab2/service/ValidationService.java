package io.github.rosgard.lab2.service;

public class ValidationService {

    private static final double[] ALLOWED_R = {1.0, 1.5, 2.0, 2.5, 3.0};

    public void validate(double x, double y, double r) {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(r)) {
            throw new IllegalArgumentException("Параметры должны быть конечными числами");
        }
        if (!isAllowedR(r)) {
            throw new IllegalArgumentException("Недопустимый R: " + r + ". Разрешены: 1, 1.5, 2, 2.5, 3");
        }
        if (!(y > -5 && y < 5)) {
            throw new IllegalArgumentException("Y вне диапазона (-5; 5): " + y);
        }
    }

    private boolean isAllowedR(double r) {
        for (double a : ALLOWED_R) {
            if (Math.abs(r - a) < 1e-9) return true;
        }
        return false;
    }
}