/**
 * Created on 09.04.2016.
 *
 * @author ymeke
 */
package ba.ciel5.featureExtractor.utils;

import ba.ciel5.featureExtractor.model.FeatureValue;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;


/**
 * Util-Class containing functions to handle Hibernate stuff.
 * First and foremost, it loads the configuration and stores the sessionFactory.
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;

    /*
    Not sure if this is the best solution, but it seemed to be the most sensible approach from
    http://stackoverflow.com/questions/24155264/return-sessionfactory-singleton-object-for-hibernate-4-3
     */
    static {
        Configuration configuration = new Configuration();
        configuration.configure();
        configuration.addAnnotatedClass(FeatureValue.class);
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    /**
     * Return the active sessionFactory.
     *
     * @return The SessionFactory object
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Open a new DB session with the active sessionFactory.
     *
     * @return The Session object
     */
    public static Session openSession() {
        return sessionFactory.openSession();
    }
}
