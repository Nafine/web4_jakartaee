package se.ifmo.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import se.ifmo.api.request.HitRequest;
import se.ifmo.api.response.HitResponse;
import se.ifmo.entity.Dot;
import se.ifmo.entity.User;
import se.ifmo.filter.auth.AuthRequired;
import se.ifmo.security.session.SessionStore;
import se.ifmo.service.HitChecker;
import se.ifmo.service.db.DotService;
import se.ifmo.service.db.UserService;

import java.util.List;

@Path("/api")
@ApplicationScoped
public class HitResource {
    private static final Gson gson = new Gson();
    private static final Gson exposeGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Inject
    private DotService dotService;
    @Inject
    private UserService userService;
    @Inject
    private HitChecker hitChecker;
    @Inject
    private SessionStore sessionStore;

    @GET
    @Path("dots")
    @AuthRequired
    public String getDots(@QueryParam("page") String page, @QueryParam("size") String size, @CookieParam("sessionid") Cookie cookie) {
        List<Dot> dots = dotService.getDotsByUid(sessionStore.getUid(cookie.getValue()));
        int pageNum = Integer.parseInt(page);
        int pageSize = Integer.parseInt(size);

        int from = (pageNum - 1) * pageSize;
        int to = Math.min(dots.size(), pageNum * pageSize);

        return String.format("""
                {"dots" : %s}
                """, exposeGson.toJson(dots.subList(from, to)));
    }

    @POST
    @Path("hit")
    @AuthRequired
    public String hit(String hitJson, @CookieParam("sessionid") Cookie cookie) {
        HitRequest hit = gson.fromJson(hitJson, HitRequest.class);

        long begin = System.currentTimeMillis();

        boolean success = hitChecker.checkHit(hit);
        HitResponse resp = new HitResponse(success, System.currentTimeMillis() - begin);

        Long uid = sessionStore.getUid(cookie.getValue());
        User client = userService.getUserById(uid);
        dotService.putDot(new Dot(hit, resp, client));

        return gson.toJson(resp);
    }
}
