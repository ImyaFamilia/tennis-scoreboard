package imya.tennis.util;

import imya.tennis.model.Match;
import imya.tennis.model.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class UtilSession {
    private static SessionFactory sessionFactory;

    // It would be nice to find a way to interpolate constraint messages depending on user's language setting,
    // but it's too much effort for a pet project
    // There's probably a way to make it by using custom helper object in thymeleaf context,
    // but it's perhaps not a best practice and a much preferable way to make it is by using Hibernate's resources
    private static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration()
                .addAnnotatedClass(Match.class)
                .addAnnotatedClass(Player.class)
                .configure();
            sessionFactory = configuration.buildSessionFactory();
        }

        return sessionFactory;
    }

    public static Session getCurrent() {
        return getSessionFactory().getCurrentSession();
    }
}
