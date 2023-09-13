package imya.tennis.dao;

import imya.tennis.util.UtilSession;
import imya.tennis.model.Match;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class MatchDao implements Dao<Match> {
    @Override
    public void saveAll(Iterable<Match> iterable) {
        Transaction transaction = null;

        try (Session session = UtilSession.getCurrent()) {
            transaction = session.beginTransaction();
            for (Match match : iterable) {
                session.persist(match);
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
    public void save(Match match) {
        Transaction transaction = null;

        try (Session session = UtilSession.getCurrent()) {
            transaction = session.beginTransaction();
            session.persist(match);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<Match> read(long id) {
        try (Session session = UtilSession.getCurrent()) {
            Transaction transaction = session.beginTransaction();
            Match match = session.get(Match.class, id);
            transaction.commit();

            return Optional.of(match);
        }
    }

    @Override
    public List<Match> readAll() {
        try (Session session = UtilSession.getCurrent()) {
            Transaction transaction = session.beginTransaction();
            List<Match> matches = session.createQuery("FROM Match", Match.class).getResultList();
            transaction.commit();

            return matches;
        }
    }

    public List<Match> readByPage(int itemsPerPage, int page) {
        try (Session session = UtilSession.getCurrent()) {
            int startsAt = itemsPerPage * (page - 1);

            Transaction transaction = session.beginTransaction();
            List<Match> matches = session.createQuery("FROM Match", Match.class)
                .setFirstResult(startsAt)
                .setMaxResults(itemsPerPage)
                .getResultList();
            transaction.commit();

            return matches;
        }
    }

    public List<Match> readByPageAlike(int itemsPerPage, int page, String alike) {
        try (Session session = UtilSession.getCurrent()) {
            int startsAt = itemsPerPage * (page - 1);

            Transaction transaction = session.beginTransaction();
            List<Match> matches = session.createQuery("FROM Match " +
                    "WHERE firstPlayer.name LIKE CONCAT('%', :alike, '%') OR " +
                    "secondPlayer.name LIKE CONCAT('%', LOWER(:alike), '%')", Match.class)
                .setFirstResult(startsAt)
                .setMaxResults(itemsPerPage)
                .setParameter("alike", alike.trim())
                .getResultList();
            transaction.commit();

            return matches;
        }
    }

    public long readCountAlike(String alike) {
        try (Session session = UtilSession.getCurrent()) {
            Transaction transaction = session.beginTransaction();
            long count = session.createQuery("SELECT COUNT(*) FROM Match " +
                "WHERE firstPlayer.name LIKE CONCAT('%', :alike, '%') OR secondPlayer.name LIKE CONCAT('%', LOWER(:alike), '%')", Long.class)
                .setParameter("alike", alike.trim())
                .getSingleResult();
            transaction.commit();

            return count;
        }
    }

    public long readCount() {
        try (Session session = UtilSession.getCurrent()) {
            Transaction transaction = session.beginTransaction();

            long count = session.createQuery("SELECT COUNT(*) FROM Match", Long.class).getSingleResult();
            transaction.commit();

            return count;
        }
    }
}
