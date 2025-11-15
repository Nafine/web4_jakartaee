package se.ifmo.api.hit;

public record HitResponse(
        Boolean hit,
        Long executionTime) {
}
