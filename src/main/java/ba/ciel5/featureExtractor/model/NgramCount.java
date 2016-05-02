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
public class NgramCount implements Serializable {

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
    public NgramCount() {

    }

    /**
     * @param ngramId   The String representation of this ngram.
     * @param versionId The UUID of the version this ngram count belongs to.
     * @param count     The amount of occurences of this ngram for this version.
     */
    public NgramCount(String ngramId, String versionId, int count) {
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
     * Set the feature value for a version. If a value already exists, it will be updated.
     *
     * @param ngramId   The String representation of this ngram.
     * @param versionId The UUID of the version this ngram count belongs to.
     * @param count     The amount of occurences of this ngram for this version.
     * @return The FeatureValue object which was subject to the change.
     */
    public static NgramCount addOrUpdateNgramCount(String ngramId, String versionId, int count) {
        Session session = HibernateUtil.openSession();
        NgramCount ngramCount = addOrUpdateNgramCount(ngramId, versionId, count, session);
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
    public static NgramCount addOrUpdateNgramCount(String ngramId, String versionId, int count, Session session) throws HibernateException {
        NgramCount ngramCount = null;
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            ngramCount = new NgramCount(ngramId, versionId, count);
            session.saveOrUpdate(ngramCount);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            session.close();
            throw new HibernateException(e.getMessage());
        }
        return ngramCount;
    }

}
