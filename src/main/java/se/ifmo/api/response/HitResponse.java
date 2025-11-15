package se.ifmo.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HitResponse {
    private Boolean hit;
    private Long executionTime;
}
