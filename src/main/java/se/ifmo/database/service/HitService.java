package se.ifmo.database.service;

import jakarta.enterprise.context.ApplicationScoped;
import se.ifmo.api.hit.HitRequest;

@ApplicationScoped
public class HitService {

    public boolean checkHit(HitRequest hitRequest) {
        double x = hitRequest.x();
        double y = hitRequest.y();
        double r = hitRequest.r();

        return (Math.abs(x) <= r && Math.abs(y) <= r
                && ((x <= 0 && y <= 0 && (Math.abs(x) + 2 * Math.abs(y) <= r))
                || (x >= 0 && x <= r && y >= 0 && y <= r / 2)
                || (x >= 0 && y <= 0 && (x * x + y * y <= r * r))));
    }
}
