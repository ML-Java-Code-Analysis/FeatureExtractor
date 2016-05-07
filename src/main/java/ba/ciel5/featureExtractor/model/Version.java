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

    @Column(name = "file_id", nullable=false)
    private String fileId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="file_id",referencedColumnName="id", insertable=false, updatable=false)
    private File file;

    @Column(name = "commit_id", nullable=false)
    private String commitId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="commit_id",referencedColumnName="id", insertable=false, updatable=false)
    private Commit commit;

    @Column(name = "path")
    private String path;

    @Column(name = "lines_added")
    private Integer linesAdded;

    @Column(name = "lines_deleted")
    private Integer linesDeleted;

    @Column(name = "file_size")
    private Integer fileSize;

    @Column(name = "deleted")
    private Boolean deleted;

    /**
     * Default constructor Hibernate ORM. Do not use this constructor.
     */
    Version() {

    }

    public Version(String id) {
        this.id = id;
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

    public String getPath() {
        return path;
    }

    public Boolean getDeleted() {
        return deleted;
    }
}
