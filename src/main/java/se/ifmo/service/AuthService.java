package se.ifmo.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import se.ifmo.database.entity.RefreshToken;
import se.ifmo.database.repository.TokenRepository;
import se.ifmo.database.repository.UserRepository;
import se.ifmo.security.SecurityUtil;
import se.ifmo.security.exception.AuthException;
import se.ifmo.security.model.Token;
import se.ifmo.security.model.TokenPair;
import se.ifmo.security.model.UserPrincipal;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class AuthService {
    private static final SecretKey KEY =
            Keys.hmacShaKeyFor("MySuperSecretKey32BytesExactly!!".getBytes(StandardCharsets.UTF_8));
    private static final int ACCESS_TOKEN_EXPIRATION_MS = 1000 * 60 * 5; //10 minutes
    private static final int REFRESH_TOKEN_EXPIRATION_MS = 1000 * 60 * 60 * 24 * 14; //14 days

    private final JwtParser jwtParser = Jwts.parser().verifyWith(KEY).build();

    @Inject
    private TokenRepository tokenRepository;
    @Inject
    private SecurityUtil securityUtil;
    @Inject
    private UserRepository userRepository;

    public TokenPair generatePair(String userId) {
        String accessToken = generateAccessToken(userId);
        String refreshToken = generateRefreshToken(userId);

        return new TokenPair(
                new Token(accessToken, ACCESS_TOKEN_EXPIRATION_MS),
                new Token(refreshToken, REFRESH_TOKEN_EXPIRATION_MS));
    }

    public UserPrincipal validateAccessToken(String token) throws AuthException {
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            return new UserPrincipal(claims.getSubject(), Set.of());
        } catch (JwtException e) {
            throw new AuthException("Failed to decode token: " + e.getMessage());
        }
    }

    public TokenPair refreshPair(String token) throws AuthException {
        Claims claims;
        try {
            claims = jwtParser.parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            throw new AuthException("Could not decode token: " + e.getMessage());
        }

        String tokenId = claims.getSubject();
        String userId = claims.get("userId", String.class);

        RefreshToken refreshToken = tokenRepository.getToken(tokenId);

        if (refreshToken != null && !refreshToken.isRevoked()) {
            tokenRepository.revokeToken(tokenId);
            return generatePair(userId);
        }

        throw new AuthException("Invalid refresh token");
    }

    public void invalidateRefresh(String token) throws AuthException {
        Claims claims;
        try {
            claims = jwtParser.parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            throw new AuthException("Could not decode token");
        }

        String tokenId = claims.getSubject();
        String userId = claims.get("userId", String.class);

        RefreshToken refreshToken = tokenRepository.getToken(tokenId);

        if (refreshToken != null && refreshToken.getOwner().getUuid().equals(UUID.fromString(userId)))
            tokenRepository.revokeToken(tokenId);
    }

    private String generateAccessToken(String userId) {
        return buildJWT(userId, Map.of(), ACCESS_TOKEN_EXPIRATION_MS);
    }

    private String generateRefreshToken(String userId) {
        String tokenId = securityUtil.generateString();
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenId(tokenId)
                .owner(userRepository.getUserById(UUID.fromString(userId)))
                .expire(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))
                .revoked(false)
                .build();
        tokenRepository.addToken(refreshToken);

        return buildJWT(tokenId, Map.of("userId", userId), REFRESH_TOKEN_EXPIRATION_MS);
    }

    private String buildJWT(String sub, Map<String, Object> claims, long expirationMs) {
        return Jwts.builder()
                .subject(sub)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(KEY)
                .compact();
    }
}
