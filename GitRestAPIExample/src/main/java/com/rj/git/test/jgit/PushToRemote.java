package com.rj.git.test.jgit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class PushToRemote {

    private static final String REMOTE_URL = "https://github.com/infobelt/oam-onboarding.git";

    public static void main(String[] args) throws IOException, GitAPIException {

        File localPath = File.createTempFile("REStGitRepository", "");
        if(!localPath.delete()) {
            throw new IOException("Could not delete temporary file " + localPath);
        }


        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
        try (Git git = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider("Gouravk1996", "Sour@v@1996"))
                .setDirectory(localPath)
                .call()) {

            Repository repository = git.getRepository();

            File theDir = new File(repository.getDirectory().getParent(), "dir1");
            theDir.mkdir();


            File myfile = new File(theDir, "testfile2.txt");
            if(!myfile.createNewFile()) {
                throw new IOException("Could not create file " + myfile);
            }


            git.add().addFilepattern(".").call();


            git.commit().setMessage("Commit all changes including additions").call();

            try(PrintWriter writer = new PrintWriter(myfile)) {
                writer.append("Hello, world!");
            }

            git.commit()
                    .setAll(true)
                    .setMessage("Commit changes to all files")
                    .call();
            // now open the created repository
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            try (Repository repository1 = builder.setGitDir(localPath)
                    .readEnvironment()
                    .findGitDir()
                    .build()) {

                try (Git git1 = new Git(repository1)) {
                    git1.push().call();
                }
                System.out.println("Pushed from repository: " + repository1.getDirectory() + " to remote repository at " + REMOTE_URL);
            }
        }
    }

}
