package se.ifmo.service.db;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import se.ifmo.entity.User;

import java.util.List;

@ApplicationScoped
public class UserService {
    @PersistenceContext(unitName = "web4_persistence")
    private EntityManager em;

    @Transactional
    public void addUser(User user) {
        em.persist(user);
    }

    public List<User> getAllUsers() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public User getUserByName(String name) {
        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
                .setParameter("name", name)
                .getResultList();
        return users.isEmpty() ? null : users.getFirst();
    }

    public User getUserById(Long uid){
        return em.getReference(User.class, uid);
    }

    @Transactional
    public void clearUsers() {
        em.createQuery("DELETE FROM User").executeUpdate();
    }
}
