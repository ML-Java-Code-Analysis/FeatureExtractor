package ba.ciel5.featureExtractor.model;

/**
 * Created on 15.04.2016.
 *
 * @author tobias.meier
 */

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;


@Entity
@Table(name = "repository")
public class Repository {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @OneToMany(mappedBy = "repository", fetch=FetchType.LAZY)
    private Collection<File> files = new ArrayList<File>();

    @OneToMany(mappedBy = "repository", fetch=FetchType.LAZY)
    private Collection<Commit> commits = new ArrayList<Commit>();

    /**
     * Default constructor Hibernate ORM. Do not use this constructor.
     */
    Repository() {

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Collection<File> getFiles() {
        return files;
    }

    public Collection<Commit> getCommits() {
        return commits;
    }
}
