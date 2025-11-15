package se.ifmo.security.session;

import jakarta.enterprise.context.ApplicationScoped;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SessionStore {
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public String generateSession(Long uid) {
        SecureRandom random = new SecureRandom();
        byte[] id = new byte[32];
        random.nextBytes(id);

        boolean contains = sessions.containsKey(Base64.getEncoder().encodeToString(id));

        while (contains) {
            random.nextBytes(id);
            contains = sessions.containsKey(Base64.getEncoder().encodeToString(id));
        }

        sessions.put(Base64.getEncoder().encodeToString(id), uid);
        System.out.println("Added " + Base64.getEncoder().encodeToString(id) + " to session store");
        return Base64.getEncoder().encodeToString(id);
    }

    public boolean isValid(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    public Long getUid(String sessionid) {
        return sessions.get(sessionid);
    }
}
