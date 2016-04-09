package repository;

import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.InvalidObjectIdException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Created on 05.04.2016.
 *
 * @author tobias.meier
 */
public class Git {

    private FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
    private Repository repository;

    /**
     * Initialize git repository
     * @args Repositorypath
     * @return Nothing.
     * @exception IOException On input error.
     * @see IOException
     */
    public Git(String path){

        String repositoryPath = path;
        String gitPath = repositoryPath + "\\.git";

        repositoryBuilder.setMustExist(true);
        repositoryBuilder.setGitDir(new File(gitPath));
        try {
            repository = repositoryBuilder.build();
        } catch (IOException e) {
            // TODO Entscheidung Programmabbruch OK? Ja
            // TODO Logging
            e.printStackTrace();
        }
    }

    /**
     * Searches for file in commit and returns the source-code
     * @args path of the file, commit id (SHA string)
     * @return source code fo file
     * @exception IOException On input error.
     * @exception FileNotFoundException When file is not found in commit
     * @see IOException
     */
    public char[] getSourceCode(String path, String commitSHA) throws FileNotFoundException {
        char[] code = null;

        Map<String, ObjectId> files = getFiles(commitSHA);

        if ( !files.containsKey(path) )
            throw new FileNotFoundException("file " + path + " not found in commit " + commitSHA);

        try {
            ObjectId file = files.get(path);
            ObjectLoader loader = repository.open(file);
            InputStream in = loader.openStream();
            String codeString = IOUtils.toString(in, "utf8");
            code = codeString.toCharArray();

        } catch (IOException e) {
            e.printStackTrace();
            // TODO Entscheidung Programmabbruch OK? Nein??
            // TODO Logging
        }

        return code;
    }

    /**
     * List all files for commit
     * @args commit id (SHA string)
     * @return hashmap(Path: FileId) of all files
     * @exception InvalidObjectIdException If commit is not found
     * @exceotion IncorrectObjectTypeException ??
     * @exceotion CorruptObjectException ??
     * @exceotion MissingObjectException ??
     * @exceotion IOException if file could not be acccess during file scan
     */
    private Map<String, ObjectId> getFiles(String commitSHA) {

        Map<String, ObjectId> files = new HashMap<String, ObjectId>();

        RevWalk walk = new RevWalk(repository);
        ObjectId commitId = null;
        try {
            commitId = ObjectId.fromString(commitSHA);
        } catch ( InvalidObjectIdException e ) {
            // TODO Entscheidung Programmabbruch OK? Nein ???
            // TODO Logging
            e.printStackTrace();
        }
        try {
            RevCommit commit = walk.parseCommit(commitId);

            RevTree tree = commit.getTree();
            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(false);
            while (treeWalk.next()) {
                if (treeWalk.isSubtree()) {
                    treeWalk.enterSubtree();
                } else {
                    files.put(treeWalk.getPathString(), treeWalk.getObjectId(0));
                }
            }
            walk.dispose();

        }
        // TODO Entscheidung Programmabbruch OK? Nein ???
        // TODO Logging
        catch (IncorrectObjectTypeException e) {
            e.printStackTrace();
        } catch (CorruptObjectException e) {
            e.printStackTrace();
        } catch (MissingObjectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    /**
     * Close Repository
     * @return Nothing
     */
    public void closeRepository() {
        repository.close();
    }
}
