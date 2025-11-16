package se.ifmo.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.codec.digest.DigestUtils;
import se.ifmo.database.entity.User;
import se.ifmo.database.repository.UserRepository;
import se.ifmo.security.SecurityUtil;
import se.ifmo.security.exception.InvalidCredentials;
import se.ifmo.security.model.TokenPair;

import java.util.UUID;

@ApplicationScoped
public class UserService {
    @Inject
    private SecurityUtil securityUtil;
    @Inject
    private UserRepository userRepository;
    @Inject
    private AuthService authService;

    public TokenPair register(String login, String password) throws InvalidCredentials {
        if (userRepository.exists(login))
            throw new InvalidCredentials("Such user already exists");

        String salt = securityUtil.generateString();
        User user = User.builder()
                .name(login)
                .passwordHash(DigestUtils.sha256Hex(password + salt))
                .salt(salt)
                .build();

        userRepository.saveUser(user);

        return authService.generatePair(user.getUuid().toString());
    }

    public TokenPair login(String login, String password) throws InvalidCredentials {
        User storedUser = userRepository.getUserByName(login);
        if (storedUser == null || !storedUser.getPasswordHash().equals(
                securityUtil.hashStr(password + storedUser.getSalt()))) {
            throw new InvalidCredentials("Invalid credentials");
        }

        return authService.generatePair(storedUser.getUuid().toString());
    }

    public User getUser(String name) {
        return userRepository.getUserByName(name);
    }
}
