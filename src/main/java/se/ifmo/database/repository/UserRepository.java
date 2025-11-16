package se.ifmo.database.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import se.ifmo.database.entity.User;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserRepository {
    @PersistenceContext(unitName = "web4_persistence")
    private EntityManager em;

    @Transactional
    public void saveUser(User user) {
        em.persist(user);
    }

    public User getUserByName(String name) {
        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
                .setParameter("name", name)
                .getResultList();
        return users.isEmpty() ? null : users.getFirst();
    }

    public User getUserById(UUID uuid) {
        return em.getReference(User.class, uuid);
    }

    public boolean exists(String name) {
        return em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult() > 0;
    }
}
