package se.ifmo.database.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import se.ifmo.database.entity.RefreshToken;
import se.ifmo.security.SecurityUtil;

@ApplicationScoped
public class TokenRepository {
    @PersistenceContext(unitName = "web4_persistence")
    private EntityManager em;

    @Inject
    private SecurityUtil securityUtil;

    @Transactional
    public void addToken(RefreshToken token) {
        em.persist(token);
    }

    @Transactional
    public void revokeToken(String tokenId) {
        em.createQuery("UPDATE RefreshToken t SET t.revoked = true WHERE t.tokenId = :tokenId", RefreshToken.class)
                .setParameter("tokenId", tokenId)
                .executeUpdate();
    }

    public RefreshToken getToken(String tokenId) {
        return em.createQuery("SELECT t FROM RefreshToken t WHERE t.tokenId = :tokenId", RefreshToken.class)
                .setParameter("tokenId", tokenId)
                .getSingleResult();
    }
}
