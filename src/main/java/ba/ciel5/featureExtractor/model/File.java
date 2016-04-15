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
@Table(name = "file")
public class File {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "repository_id")
    private Integer repositoryId;

    @Column(name = "language")
    private String language;

    @OneToMany
    private Collection<Version> versions = new ArrayList<Version>();

    /**
     * Default constructor Hibernate ORM. Do not use this constructor.
     */
    public File() {

    }

    public String getId() {
        return id;
    }

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public String getLanguage() {
        return language;
    }
}
