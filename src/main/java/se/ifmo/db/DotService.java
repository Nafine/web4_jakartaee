package se.ifmo.db;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import se.ifmo.entity.Dot;

import java.util.List;

@ApplicationScoped
public class DotService {
    @PersistenceContext(unitName = "web4_persistence")
    private EntityManager em;

    public List<Dot> getDotsByLogin(String login) {
        return em.createQuery("""
                SELECT d FROM Dot d
                WHERE d.user.login = :login 
                ORDER BY d.id
                """, Dot.class)
                .setParameter("login", login)
                .getResultList();
    }
}
