package se.ifmo.resource;

import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import se.ifmo.db.DotService;
import se.ifmo.entity.Dot;
import se.ifmo.filter.auth.AuthRequired;

import java.util.List;

@Path("/api")
@ApplicationScoped
public class DotResource {
    private static final Gson gson = new Gson();
    @Inject
    private DotService dotService;

    @GET
    @Path("dots/{page}")
    @AuthRequired
    public String getDots(@PathParam("page") String page) {
        List<Dot> dots = dotService.getDotsByLogin("oleg");
        return gson.toJson(dots);
    }
}
