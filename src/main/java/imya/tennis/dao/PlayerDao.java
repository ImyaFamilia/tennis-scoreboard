package imya.tennis.dao;

import imya.tennis.util.UtilSession;
import imya.tennis.model.Player;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class PlayerDao implements Dao<Player> {
    @Override
    public void saveAll(Iterable<Player> iterable) {
        Transaction transaction = null;

        try (Session session = UtilSession.getCurrent()) {
            transaction = session.beginTransaction();
            for (Player value : iterable) {
                session.persist(value);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public void save(Player player) {
        Transaction transaction = null;

        try (Session session = UtilSession.getCurrent()) {
            transaction = session.beginTransaction();
            session.persist(player);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<Player> read(long id) {
        try (Session session = UtilSession.getCurrent()) {
            Transaction transaction = session.beginTransaction();
            Player player = session.get(Player.class, id);
            transaction.commit();

            return Optional.of(player);
        }
    }

    public Optional<Player> saveOrReadExisting(Player player) {
        try {
            save(player);
        } catch (PersistenceException e) {
            player = readByName(player.getName()).get();
        }

        return Optional.ofNullable(player);
    }

    public Optional<Player> readByName(String name) {
        try (Session session = UtilSession.getCurrent()) {
            Transaction transaction = session.beginTransaction();

            Query query = session.createQuery("FROM Player WHERE name = :name", Player.class);
            query.setParameter("name", name);
            Player player = (Player) query.getSingleResult();

            transaction.commit();
            return Optional.of(player);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Player> readAll() {
        try (Session session = UtilSession.getCurrent()) {
            Transaction transaction = session.beginTransaction();
            List<Player> players = session.createQuery("FROM Player", Player.class).getResultList();
            transaction.commit();

            return players;
        }
    }
}
