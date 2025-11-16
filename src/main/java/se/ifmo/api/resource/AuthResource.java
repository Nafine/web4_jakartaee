package se.ifmo.api.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import se.ifmo.api.dto.auth.LoginRequest;
import se.ifmo.api.dto.auth.TokenResponse;
import se.ifmo.security.exception.AuthException;
import se.ifmo.security.exception.InvalidCredentials;
import se.ifmo.security.model.Token;
import se.ifmo.security.model.TokenPair;
import se.ifmo.service.AuthService;
import se.ifmo.service.UserService;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    @Inject
    private UserService userService;
    @Inject
    private AuthService authService;


    @POST
    @Path("register")
    public Response register(@Valid LoginRequest req) {
        try {
            TokenPair tokenPair = userService.register(req.login(), req.password());

            return makeTokenResponse(tokenPair);
        } catch (InvalidCredentials e) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @Path("login")
    @POST
    public Response login(@Valid LoginRequest req) {
        try {
            TokenPair tokenPair = userService.login(req.login(), req.password());

            return makeTokenResponse(tokenPair);
        } catch (InvalidCredentials e) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @Path("/refresh")
    @POST
    public Response refresh(@NotNull @CookieParam("refresh-token") String refreshToken) {
        try {
            TokenPair tokenPair = authService.refreshPair(refreshToken);
            return makeTokenResponse(tokenPair);
        } catch (AuthException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path("logout")
    @POST
    public Response logout(@CookieParam("refresh-token") Cookie cookie) {
        try {
            authService.invalidateRefresh(cookie.getValue());
            return Response.ok().build();
        } catch (AuthException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    private NewCookie makeRefreshCookie(Token refreshToken) {
        return new NewCookie.Builder("refresh-token")
                .value(refreshToken.token())
                .maxAge(refreshToken.expires())
                .path("/")
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.LAX)
                .build();
    }

    private Response makeTokenResponse(TokenPair tokenPair) {
        TokenResponse response = new TokenResponse(
                tokenPair.access().token(),
                tokenPair.access().expires()
        );
        return Response
                .ok(response)
                .cookie(makeRefreshCookie(tokenPair.refresh()))
                .build();
    }
}
