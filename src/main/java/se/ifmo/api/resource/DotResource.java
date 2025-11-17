package se.ifmo.api.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import se.ifmo.api.dto.dot.DotDto;
import se.ifmo.api.dto.dot.PageDto;
import se.ifmo.security.filter.auth.AuthRequired;
import se.ifmo.service.DotService;

import java.security.Principal;

@Path("/")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@AuthRequired
public class DotResource {
    @Inject
    private SecurityContext securityContext;
    @Inject
    private DotService dotService;

    @GET
    @Path("dots")
    public PageDto getDots(@DefaultValue("1") @QueryParam("page") int page,
                           @DefaultValue("10") @QueryParam("size") int size) {
        Principal userPrincipal = securityContext.getUserPrincipal();
        return dotService.getPage(page, size, userPrincipal.getName());
    }

    @GET
    @Path("dots/last")
    public PageDto getLastPage(@DefaultValue("10") @QueryParam("size") int size) {
        Principal userPrincipal = securityContext.getUserPrincipal();
        return dotService.getLastPage(size, userPrincipal.getName());
    }

    @POST
    @Path("hit")
    public Response hit(@Valid DotDto dotDto) {
        Principal userPrincipal = securityContext.getUserPrincipal();
        dotService.saveDot(dotDto, userPrincipal.getName());
        return Response.ok().build();
    }
}
