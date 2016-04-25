/**
 * Created by tobias.meier on 24.04.2016.
 */
package ba.ciel5.featureExtractor.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "issue")
public class Issue {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "type")
    private String type;

    //@ManyToMany(fetch=FetchType.LAZY)
    @ManyToMany(fetch=FetchType.EAGER)
    private Collection<Commit> commits = new ArrayList<Commit>();

    /**
     * Default constructor Hibernate ORM. Do not use this constructor.
     */
    Issue() {

    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public Collection<Commit> getCommits() {
        return commits;
    }
}
