/**
 * Created on 09.04.2016.
 *
 * @author ymeke
 */

package ba.ciel5.featureExtractor.model;

import ba.ciel5.featureExtractor.utils.HibernateUtil;
import org.hibernate.HibernateException;
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
    @Column(name = "version_id")
    private String versionId;

    @Id
    @Column(name = "ngram_size")
    private Integer nGramSize;

    @Id
    @Column(name = "ngram_level")
    private Integer nGramLevel;

    @Column(name = "vector_size")
    private Integer vectorSize;

    @Column(name = "ngram_values")
    private String nGramValues;

    /**
     * Default constructor Hibernate ORM. Do not use this constructor.
     */
    public NGramVector() {

    }


    public NGramVector(String versionId, Integer nGramSize, Integer nGramLevel, Integer vectorSize, String nGramValues) {
        this.versionId = versionId;
        this.nGramSize = nGramSize;
        this.nGramLevel = nGramLevel;
        this.vectorSize = vectorSize;
        this.nGramValues = nGramValues;
    }

    public Integer getnGramSize() {
        return nGramSize;
    }

    public String getVersionId() {
        return versionId;
    }

    public Integer getnGramLevel() {
        return nGramLevel;
    }

    public String getnGramValues() {
        return nGramValues;
    }

    public void setnGramSize(Integer nGramSize) {
        this.nGramSize = nGramSize;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public void setnGramLevel(Integer nGramLevel) {
        this.nGramLevel = nGramLevel;
    }

    public void setnGramValues(String nGramValues) {
        this.nGramValues = nGramValues;
    }

    /**
     * Set the ngram count for a version. If a value already exists, it will be updated.
     * ATTENTION: this method has a bug
     * @param size
     * @param versionId
     * @param level
     * @param values
     * @return
     */
    @Deprecated
    public static NGramVector addOrUpdateNGramVector(String versionId, Integer size, Integer level, Integer vectorSize, String values) {
        Session session = HibernateUtil.openSession();
        NGramVector ngramVector = null;
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            ngramVector = addOrUpdateNGramVector(versionId, size , level, vectorSize, values);
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
    public static NGramVector addOrUpdateNGramVector(String versionId, Integer size, Integer level, Integer vectorSize, String values, Session session) {
        NGramVector ngramVector = new NGramVector(versionId, size, level, vectorSize, values);
        session.saveOrUpdate(ngramVector);
        return ngramVector;
    }
}
