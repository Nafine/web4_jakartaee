package se.ifmo.security.session;

import jakarta.enterprise.context.ApplicationScoped;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SessionStore {
    private final Set<String> sessions = ConcurrentHashMap.newKeySet();

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public String generateSession() {
        SecureRandom random = new SecureRandom();
        byte[] id = new byte[32];
        random.nextBytes(id);

        boolean contains = sessions.contains(Base64.getEncoder().encodeToString(id));

        while (contains) {
            random.nextBytes(id);
            contains = sessions.contains(Base64.getEncoder().encodeToString(id));
        }

        return Base64.getEncoder().encodeToString(id);
    }

    public boolean isValid(String sessionId) {
        return sessions.contains(sessionId);
    }
}
