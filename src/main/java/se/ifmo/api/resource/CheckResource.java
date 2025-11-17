package se.ifmo.api.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import se.ifmo.security.filter.auth.AuthRequired;

@Path("/")
public class CheckResource {
    @Path("check")
    @POST
    @AuthRequired
    public Response check() {
        return Response.ok().build();
    }
}
