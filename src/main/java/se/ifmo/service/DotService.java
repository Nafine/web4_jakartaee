package se.ifmo.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import se.ifmo.api.dto.dot.DotDto;
import se.ifmo.database.entity.Dot;
import se.ifmo.database.mapper.DotMapper;
import se.ifmo.database.mapper.DotMapperImpl;
import se.ifmo.database.repository.DotRepository;
import se.ifmo.database.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DotService {
    @Inject
    private DotMapper dotMapper;
    @Inject
    private DotRepository dotRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private DotMapperImpl dotMapperImpl;
    @Inject
    private UserService userService;

    private boolean checkHit(DotDto hitRequest) {
        double x = hitRequest.x();
        double y = hitRequest.y();
        double r = hitRequest.r();

        return (Math.abs(x) <= r && Math.abs(y) <= r
                && ((x <= 0 && y <= 0 && (Math.abs(x) + 2 * Math.abs(y) <= r))
                || (x >= 0 && x <= r && y >= 0 && y <= r / 2)
                || (x >= 0 && y <= 0 && (x * x + y * y <= r * r))));
    }

    public void saveDot(DotDto dotDto, String userId) {
        Dot dot = dotMapperImpl.toEntity(dotDto)
                .setResult(checkHit(dotDto))
                .setExecutionTime(100)
                .setOwner(userRepository.getUserById(UUID.fromString(userId)));

        dotRepository.saveDot(dot);
    }

    public List<Dot> getDots(int page, int size, String userId) {
        List<Dot> dots = dotRepository.getDotsByUuid(UUID.fromString(userId));

        int from = (page - 1) * size;
        int to = Math.min(dots.size(), page * size);

        return dots.subList(from, to);
    }
}
