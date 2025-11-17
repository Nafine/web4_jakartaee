package se.ifmo.api.dto.dot;

import java.util.List;

public record PageDto(List<DotDto> dots, int page, boolean hasNext) {
}
