/**
 * Created on 09.04.2016.
 *
 * @author ymeke
 */
package ba.ciel5.featureExtractor.utils;

import ba.ciel5.featureExtractor.model.*;
import ba.ciel5.featureExtractor.model.Version;
import javafx.util.Pair;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
        configuration.addAnnotatedClass(Repository.class);
        configuration.addAnnotatedClass(Commit.class);
        configuration.addAnnotatedClass(File.class);
        configuration.addAnnotatedClass(Version.class);
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

    /**
     * take a string and execute a simple query (no filters)
     * @param queryString for example FROM Version (select all datasets from table version)
     * @param <T>
     * @return result set
     * @throws HibernateException
     */
    public static <T> List<T> simpleQuery(String queryString) throws HibernateException {
        return complexQuery(queryString, new ArrayList());
    }

    /**
     * take a string and execure a simple query with filters
     * @param queryString for example FROM Version WHERE name = :name
     * @param parameters Expects a list of tuples with parameters to replace in the query (like SQL prepared statements).
     *                   For example [(Name,Tobias)] --> replaces :name in query with value tobias
     * @param <T>
     * @return result set
     * @throws HibernateException
     */
    public static <T> List<T> complexQuery(String queryString, List<Pair<String, T>> parameters) throws HibernateException {
        Session session = openSession();
        Transaction tx = null;
        List<T> result = new ArrayList<T>();
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery(queryString);
            for (Pair<String, T> parameter : parameters) {
                query.setParameter(parameter.getKey(), parameter.getValue());
            }
            result = query.list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null)
                tx.rollback();
            throw new HibernateError(e.getMessage());
        } finally {
            session.close();
        }
        return result;
    }
}
