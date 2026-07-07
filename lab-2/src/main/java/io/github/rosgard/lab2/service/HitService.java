package io.github.rosgard.lab2.service;

public class HitService {

    public record PointRequest(double x, double y, double r,
                               long requestTimeMillis,
                               long clientTimeMillis,
                               String clientTimeText) { }

    public record HitResult(PointRequest request,
                            boolean hit,
                            long calcNanos,
                            long serverTimeMillis) { }

    public HitResult compute(double x, double y, double r,
                             long clientTimeMillis, String clientTimeText) {
        long reqTime = System.currentTimeMillis();
        long start = System.nanoTime();
        boolean hit = isHit(x, y, r);
        long nanos = System.nanoTime() - start;
        PointRequest pr = new PointRequest(x, y, r, reqTime, clientTimeMillis, clientTimeText);
        return new HitResult(pr, hit, nanos, System.currentTimeMillis());
    }

    private boolean isHit(double x, double y, double r) {
        boolean circle = (x >= 0 && y <= 0 && (x * x + y * y) <= Math.pow(r / 2.0, 2));
        boolean rect = (x <= 0 && y <= 0 && x >= -r && y >= -r);
        boolean tri = (x <= 0 && y >= 0 && y <= x + r);
        return circle || rect || tri;
    }
}