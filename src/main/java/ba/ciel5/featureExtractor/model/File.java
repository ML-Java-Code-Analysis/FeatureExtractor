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

    @Column(name = "repository_id", nullable=false)
    private Integer repositoryId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="repository_id",referencedColumnName="id", insertable=false, updatable=false)
    private Repository repository;

    @Column(name = "language")
    private String language;

    //@OneToMany(mappedBy = "file", fetch=FetchType.LAZY)
    @OneToMany(mappedBy = "file", fetch=FetchType.EAGER)
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

    public Repository getRepository() {
        return repository;
    }

    public String getLanguage() {
        return language;
    }

    public Collection<Version> getVersions() {
        return versions;
    }
}
