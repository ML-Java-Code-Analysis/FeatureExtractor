/**
 * Created on 09.04.2016.
 *
 * @author ymeke
 */

package ba.ciel5.featureExtractor.model;

import ba.ciel5.featureExtractor.utils.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "ngram_count")
public class NGramCount implements Serializable {

    @Id
    @Column(name = "ngram_id")
    private String ngramId;

    @Id
    @Column(name = "version_id")
    private String versionId;

    @Column(name = "count")
    private int count;

    /**
     * Default constructor Hibernate ORM. Do not use this constructor.
     */
    public NGramCount() {

    }

    /**
     * @param ngramId   The String representation of this ngram.
     * @param versionId The UUID of the version this ngram count belongs to.
     * @param count     The amount of occurences of this ngram for this version.
     */
    public NGramCount(String ngramId, String versionId, int count) {
        this.ngramId = ngramId;
        this.versionId = versionId;
        this.count = count;
    }

    public String getNgramId() {
        return ngramId;
    }

    public String getVersionId() {
        return versionId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    /**
     * Set the ngram count for a version. If a value already exists, it will be updated.
     *
     * @param ngramId   The String representation of this ngram.
     * @param versionId The UUID of the version this ngram count belongs to.
     * @param count     The amount of occurences of this ngram for this version.
     * @return The FeatureValue object which was subject to the change.
     */
    public static NGramCount addOrUpdateNgramCount(String ngramId, String versionId, int count) {
        Session session = HibernateUtil.openSession();
        NGramCount ngramCount = null;
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            ngramCount = addOrUpdateNgramCount(ngramId, versionId, count);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            session.close();
            throw new HibernateException(e.getMessage());
        }
        session.close();
        return ngramCount;
    }

    /**
     * Set the feature value for a version. If a value already exists, it will be updated.
     *
     * @param ngramId   The String representation of this ngram.
     * @param versionId The UUID of the version this ngram count belongs to.
     * @param count     The amount of occurences of this ngram for this version.
     * @param session   The DB session to use.
     * @return The FeatureValue object which was subject to the change.
     */
    public static NGramCount addOrUpdateNgramCount(String ngramId, String versionId, int count, Session session) throws HibernateException {
        NGramCount ngramCount = new NGramCount(ngramId, versionId, count);
        session.saveOrUpdate(ngramCount);
        return ngramCount;
    }

    /**
     * Delete all entries in the SQL table
     */
    public static void turncate() {
        String hql = String.format("delete from ngram_count");
        Session session = HibernateUtil.openSession();
        try {
            Query query = session.createQuery(hql);
        } catch (HibernateException e) {
            session.close();
            throw new HibernateException(e.getMessage());
        }
        session.close();
    }
}
