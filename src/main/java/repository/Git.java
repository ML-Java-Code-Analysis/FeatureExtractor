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

    private Repository repository;

    /**
     * Initialize git repository
     *
     * @throws IOException If the repository is not found.
     * @param path The Filepath to the repository root directory.
     * @see IOException
     */
    public Git(String path) throws IOException {
        String gitPath = path + "\\.git";

        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setMustExist(true);
        repositoryBuilder.setGitDir(new File(gitPath));
    }

    /**
     * Searches for file in commit and returns the source-code
     *
     * @return source code fo file
     * @throws IOException On input error, eg. when a file is not found in commit
     * @param path Filepath of the source file
     * @param commitSHA The SHA-Hash of the commit
     * @see IOException
     */
    public char[] getSourceCode(String path, String commitSHA) throws IOException {
        char[] code = null;

        Map<String, ObjectId> files = getFiles(commitSHA);

        if (!files.containsKey(path))
            throw new FileNotFoundException("file " + path + " not found in commit " + commitSHA);

        ObjectId file = files.get(path);
        ObjectLoader loader = repository.open(file);
        InputStream in = loader.openStream();
        String codeString = IOUtils.toString(in, "utf8");
        code = codeString.toCharArray();

        return code;
    }

    /**
     * @param commitSHA The SHA-Hash of the commit
     * @return A Hashmap consisting of Filepaths mapped to Object-IDs
     * @throws IOException If file could not be acccess during file scan
     */
    private Map<String, ObjectId> getFiles(String commitSHA) throws IOException {

        Map<String, ObjectId> files = new HashMap<String, ObjectId>();

        RevWalk walk = new RevWalk(repository);
        ObjectId commitId =  ObjectId.fromString(commitSHA);

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
        return files;
    }

    /**
     * Close Repository
     */
    public void closeRepository() {
        repository.close();
    }
}
