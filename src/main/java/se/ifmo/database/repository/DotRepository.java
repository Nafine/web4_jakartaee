package se.ifmo.database.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import se.ifmo.database.entity.Dot;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DotRepository {
    @PersistenceContext(unitName = "web4_persistence")
    private EntityManager em;

    public List<Dot> getDotsByUuid(UUID uuid) {
        return em.createQuery("""
                        SELECT d FROM Dot d
                        WHERE d.owner.uuid = :uuid
                        ORDER BY d.id
                        """, Dot.class)
                .setParameter("uuid", uuid)
                .getResultList();
    }

    @Transactional
    public void saveDot(Dot dot) {
        em.persist(dot);
    }
}
