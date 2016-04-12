/**
 * Created on 10.04.2016.
 *
 * @author ymeke
 */
package ba.ciel5.featureExtractor.model;

import javax.persistence.*;


@Entity
@Table(name = "version")
public class Version {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "file_id")
    private String fileId;

    @ManyToOne
    private File file;

    @Column(name = "commit_id")
    private String commitId;

    @ManyToOne
    private Commit commit;

    @Column(name = "lines_added")
    private Integer linesAdded;

    @Column(name = "lines_deleted")
    private Integer linesDeleted;

    @Column(name = "file_size")
    private Integer fileSize;

    /**
     * Default constructor Hibernate ORM. Do not use this constructor.
     */
    Version() {

    }

    public String getId() {
        return id;
    }

    public String getFileId() {
        return fileId;
    }

    public File getFile() {
        return file;
    }

    public String getCommitId() {
        return commitId;
    }

    public Commit getCommit(){
        return commit;
    }

    public Integer getLinesAdded() {
        return linesAdded;
    }

    public Integer getLinesDeleted() {
        return linesDeleted;
    }

    public Integer getFileSize() {
        return fileSize;
    }
}
