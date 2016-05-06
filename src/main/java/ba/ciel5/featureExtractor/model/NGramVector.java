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

@Entity
@Table(name = "ngram_vector")
public class NGramVector implements Serializable {

    @Id
    @Column(name = "size")
    private Integer size;

    @Id
    @Column(name = "version_id")
    private String versionId;

    @Column(name = "level")
    private Integer level;

    @Column(name = "values")
    private String values;

    /**
     * Default constructor Hibernate ORM. Do not use this constructor.
     */
    public NGramVector() {

    }


    public NGramVector(Integer size, String versionId, Integer level, String values) {
        this.size = size;
        this.versionId = versionId;
        this.level = level;
        this.values = values;
    }

    public Integer getSize() {
        return size;
    }

    public String getVersionId() {
        return versionId;
    }

    public Integer getLevel() {
        return level;
    }

    public String getValues() {
        return values;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setValues(String values) {
        this.values = values;
    }

    /**
     * Set the ngram count for a version. If a value already exists, it will be updated.
     * @param size
     * @param versionId
     * @param level
     * @param values
     * @return
     */
    public static NGramVector addOrUpdateNGramVector(Integer size, String versionId, Integer level, String values) {
        Session session = HibernateUtil.openSession();
        NGramVector ngramVector = null;
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            ngramVector = addOrUpdateNGramVector(size, versionId, level, values);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            session.close();
            throw new HibernateException(e.getMessage());
        }
        session.close();
        return ngramVector;
    }

    /**
     * Set the feature value for a version. If a value already exists, it will be updated.
     * @param size
     * @param versionId
     * @param level
     * @param values
     * @param session
     * @return
     */
    public static NGramVector addOrUpdateNGramVector(Integer size, String versionId, Integer level, String values, Session session) {
        NGramVector ngramVector = new NGramVector(size, versionId, level, values);
        session.saveOrUpdate(ngramVector);
        return ngramVector;
    }
}
