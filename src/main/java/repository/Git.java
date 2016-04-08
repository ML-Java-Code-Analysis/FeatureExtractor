package repository;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;


/**
 * Created on 05.04.2016.
 *
 * @author tobias.meier
 */
public class Git {

    FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
    Repository repository;


    public Git(int repositoryId){

        String repositoryPath = "P:\\Studium\\FS2016\\BA\\GitHubProjects\\LED-Cube-Prototyper";
        String gitPath = repositoryPath + "\\.git";

        repositoryBuilder.setMustExist(true);
        repositoryBuilder.setGitDir(new File(gitPath));
        try {
            repository = repositoryBuilder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public char[] getSourceCode(String path, String commitSHA) {
        // hier kommt cooode rein!
        char[] code = null;

        RevWalk walk = new RevWalk(repository);
        ObjectId commitId = ObjectId.fromString(commitSHA);
        try {
            RevCommit commit = walk.parseCommit(commitId);
            System.out.print(commit);
            walk.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return code;
    }

    private void closeRepository() {
        repository.close();
    }
}
