package se.ifmo.database.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import se.ifmo.database.entity.Dot;

import java.util.List;

@ApplicationScoped
public class DotRepository {
    @PersistenceContext(unitName = "web4_persistence")
    private EntityManager em;

    public List<Dot> getDotsByUid(Long uid) {
        return em.createQuery("""
                        SELECT d FROM Dot d
                        WHERE d.owner.id = :uid
                        ORDER BY d.id
                        """, Dot.class)
                .setParameter("uid", uid)
                .getResultList();
    }

    @Transactional
    public void putDot(Dot dot) {
        em.persist(dot);
    }
}
