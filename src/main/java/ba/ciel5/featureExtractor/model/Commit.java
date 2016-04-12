/**
 * Created on 10.04.2016.
 *
 * @author ymeke
 */

package ba.ciel5.featureExtractor.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "commit")
public class Commit {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "repository_id")
    private Integer repositoryId;

    @Column(name = "message")
    private String message;

    @Column(name = "author")
    private String author;

    @Column(name = "timestamp")
    private Date timestamp;

    @Column(name = "complete")
    private Boolean isComplete;

    @OneToMany
    private Collection<Version> versions = new ArrayList<Version>();

    /**
     * Default constructor Hibernate ORM. Do not use this constructor.
     */
    Commit() {

    }

    public String getId() {
        return id;
    }

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Boolean getComplete() {
        return isComplete;
    }

    public Collection<Version> getVersions() {
        return versions;
    }
}
