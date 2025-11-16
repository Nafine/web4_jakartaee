package se.ifmo.api.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import se.ifmo.api.dto.dot.DotDto;
import se.ifmo.database.mapper.DotMapper;
import se.ifmo.security.filter.auth.AuthRequired;
import se.ifmo.service.DotService;

import java.security.Principal;
import java.util.List;

@Path("/")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@AuthRequired
public class HitResource {
    @Inject
    private SecurityContext securityContext;
    @Inject
    private DotService dotService;
    @Inject
    private DotMapper dotMapper;

    @GET
    @Path("dots")
    public List<DotDto> getDots(@DefaultValue("1") @QueryParam("page") int page,
                                @DefaultValue("10") @QueryParam("size") int size) {
        Principal userPrincipal = securityContext.getUserPrincipal();
        return dotService.getDots(page, size, userPrincipal.getName()).stream().map(dotMapper::toDto).toList();
    }

    @POST
    @Path("hit")
    public Response hit(@Valid DotDto dotDto) {
        Principal userPrincipal = securityContext.getUserPrincipal();
        dotService.saveDot(dotDto, userPrincipal.getName());
        return Response.ok().build();
    }
}
