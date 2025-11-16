package se.ifmo.api.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import se.ifmo.api.dto.auth.UserDto;
import se.ifmo.database.mapper.UserMapper;
import se.ifmo.security.filter.auth.AuthRequired;
import se.ifmo.service.UserService;

@Path("/user")
@ApplicationScoped
public class UserResource {
    @Inject
    private SecurityContext securityContext;
    @Inject
    private UserMapper userMapper;
    @Inject
    private UserService userService;

    public UserResource() {
    }

    @Path("/me")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AuthRequired
    public UserDto me() {
        return userMapper.toDto(userService.getUser(securityContext.getUserPrincipal().getName()));
    }
}
