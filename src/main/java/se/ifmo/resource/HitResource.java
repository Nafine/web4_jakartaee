package se.ifmo.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import se.ifmo.api.hit.HitRequest;
import se.ifmo.api.hit.HitResponse;
import se.ifmo.database.entity.Dot;
import se.ifmo.database.entity.User;
import se.ifmo.database.repository.DotRepository;
import se.ifmo.database.repository.UserRepository;
import se.ifmo.database.service.HitService;
import se.ifmo.security.filter.auth.AuthRequired;
import se.ifmo.security.session.SessionStore;

import java.util.List;

@Path("/")
@ApplicationScoped
public class HitResource {
    @Inject
    private DotRepository dotService;
    @Inject
    private UserRepository userService;
    @Inject
    private HitService hitChecker;
    @Inject
    private SessionStore sessionStore;

    @GET
    @Path("dots")
    @Produces(MediaType.APPLICATION_JSON)
    @AuthRequired
    public List<Dot> getDots(@QueryParam("page") String page, @QueryParam("size") String size, @CookieParam("sessionid") Cookie cookie) {
        List<Dot> dots = dotService.getDotsByUid(sessionStore.getUid(cookie.getValue()));
        int pageNum = Integer.parseInt(page);
        int pageSize = Integer.parseInt(size);

        int from = (pageNum - 1) * pageSize;
        int to = Math.min(dots.size(), pageNum * pageSize);

        return dots.subList(from, to);
    }

    @POST
    @Path("hit")
    @Consumes(MediaType.APPLICATION_JSON)
    @AuthRequired
    public void hit(HitRequest req, @CookieParam("sessionid") Cookie cookie) {
        long begin = System.currentTimeMillis();

        boolean success = hitChecker.checkHit(req);
        HitResponse resp = new HitResponse(success, System.currentTimeMillis() - begin);

        Long uid = sessionStore.getUid(cookie.getValue());
        User client = userService.getUserById(uid);
        dotService.putDot(new Dot(req, resp, client));
    }
}
