package se.ifmo.service;

import jakarta.enterprise.context.ApplicationScoped;
import se.ifmo.api.request.HitRequest;

@ApplicationScoped
public class HitChecker {

    public boolean checkHit(HitRequest hitRequest) {
        double x = hitRequest.getX();
        double y = hitRequest.getY();
        double r = hitRequest.getR();

        return (Math.abs(x) <= r && Math.abs(y) <= r
                && ((x <= 0 && y <= 0 && (Math.abs(x) + 2 * Math.abs(y) <= r))
                || (x >= 0 && x <= r && y >= 0 && y <= r / 2)
                || (x >= 0 && y <= 0 && (x * x + y * y <= r * r))));
    }
}
