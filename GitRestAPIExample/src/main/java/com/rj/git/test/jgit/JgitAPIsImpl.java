package com.rj.git.test.jgit;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Service
public class JgitAPIsImpl {

	public void cloneGit() throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		Git git = Git.cloneRepository().setURI("https://github.com/rjgrover05/RealTimeProjects.git")
				.setDirectory(new File("D:\\infobelt_projects\\Jgit")).call();

		Repository repository = git.getRepository();

		System.out.println(repository.getBranch());
	}

	public void pushToRepoDefault() throws IllegalStateException, GitAPIException, IOException, URISyntaxException {
		Path repoPath = Paths.get("./Jgit_3");
		try (Git git = Git.init().setDirectory(repoPath.toFile()).call()) {

			Files.writeString(repoPath.resolve("test.txt"), "# Jgit_3");
			git.add().addFilepattern("test.txt").call();
			git.commit().setMessage("first commit for test").setAuthor("rajko5", "raj.kumar@infobelt.com").call();

			// git remote add origin git@github.com:ralscha/test_repo.git
			RemoteAddCommand remoteAddCommand = git.remoteAdd();
			remoteAddCommand.setName("origin");
			remoteAddCommand.setUri(new URIish("git@github.com:rajko5/Demo_Repo.git"));
			remoteAddCommand.call();

			// git push -u origin master
			PushCommand pushCommand = git.push();
			pushCommand.add("main");
			pushCommand.setRemote("origin");
			pushCommand.call();
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void pushToRepoWithAuth() throws IllegalStateException, GitAPIException, IOException, URISyntaxException {
		Path repoPath = Paths.get("./Jgit_3");
		try (Git git = Git.init().setDirectory(repoPath.toFile()).call()) {

			Files.writeString(repoPath.resolve("test.txt"), "# Jgit_3");
			git.add().addFilepattern("test.txt").call();
			git.commit().setMessage("first commit for test").setAuthor("rajko5", "raj.kumar@infobelt.com").call();

			// git remote add origin git@github.com:ralscha/test_repo.git
			RemoteAddCommand remoteAddCommand = git.remoteAdd();
			remoteAddCommand.setName("origin");
			remoteAddCommand.setUri(new URIish("git@github.com:rajko5/Demo_Repo.git"));
			remoteAddCommand.call();

			// git push -u origin master
			PushCommand pushCommand = git.push();
			pushCommand.setCredentialsProvider(
					new UsernamePasswordCredentialsProvider("raj.kumar@infobelt.com", "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"));
			pushCommand.add("main");
			pushCommand.setRemote("origin");
			pushCommand.call();
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void pushToRepoWithSSH() throws IllegalStateException, GitAPIException, IOException, URISyntaxException {
		Path repoPath = Paths.get("./Jgit_3");
		try (Git git = Git.init().setDirectory(repoPath.toFile()).call()) {

			JschConfigSessionFactory sshSessionFactory = new JschConfigSessionFactory() {

				@Override
				protected JSch createDefaultJSch(FS fs) throws JSchException {
					JSch defaultJSch = super.createDefaultJSch(fs);
					defaultJSch.addIdentity("D:\\infobelt_projects\\id_ed25519", "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");
					System.out.println(defaultJSch.getConfigRepository());
					// if key is protected with passphrase
					// defaultJSch.addIdentity("c:/path/to/my/private_key", "my_passphrase");

					return defaultJSch;
				}

				@Override
				protected void configure(Host hc, Session session) {
					System.out.println("HostName: " + hc.getHostName());

				}
			};

			Files.writeString(repoPath.resolve("test.txt"), "# Jgit_3");
			git.add().addFilepattern("test.txt").call();
			git.commit().setMessage("first commit for test").setAuthor("rajko5", "raj.kumar@infobelt.com").call();

			// git remote add origin git@github.com:ralscha/test_repo.git
			RemoteAddCommand remoteAddCommand = git.remoteAdd();
			remoteAddCommand.setName("origin");
			remoteAddCommand.setUri(new URIish("git@github.com:rajko5/Demo_Repo.git"));
			remoteAddCommand.call();

			System.out.println("Repository: " + remoteAddCommand.getRepository());

			// git push -u origin master
			PushCommand pushCommand = git.push();
			pushCommand.setTransportConfigCallback(transport -> {
				SshTransport sshTransport = (SshTransport) transport;
				sshTransport.setSshSessionFactory(sshSessionFactory);
			});
			pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider("raj.kumar@infobelt.com",
					"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"));
			pushCommand.add("main");
			pushCommand.setRemote("origin");
			pushCommand.call();
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void cloneAndPush() throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		Path localPath = Paths.get("./my_other_test");
		try (Git git = Git.cloneRepository().setURI("https://github.com/rjgrover05/RealTimeProjects.git")
				.setDirectory(localPath.toFile()).call()) {

			Files.writeString(localPath.resolve("SECOND.md"), "# another file");
			git.add().addFilepattern("SECOND.md").call();
			git.commit().setMessage("second commit").setAuthor("author", "author@email.com").call();

			PushCommand pushCommand = git.push();
			pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider("rjgrover05",
					"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"));
			pushCommand.add("main");
			pushCommand.setRemote("origin");
			pushCommand.call();
  
			//git.push().call();  
		}
	}

	public void commitToRepo() throws IllegalStateException, GitAPIException, IOException {
		String REMOTE_URL = "https://github.com/rjgrover05/RealTimeProjects.git";
		/*
		 * File localPath = File.createTempFile("REStGitRepository", "");
		 * if(!localPath.delete()) { throw new
		 * IOException("Could not delete temporary file " + localPath); }
		 */
		File localPath = new File("D:\\infobelt_projects\\Jgit");
//Git.cloneRepository().setURI("").setBranch("").call();
		System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
		try (Git git = Git.cloneRepository().setURI(REMOTE_URL)
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider("rjgrover05", "Raj@5684"))
				.setDirectory(localPath).call()) {

			Repository repository = git.getRepository();

			File theDir = new File(repository.getDirectory().getParent(), "dir1");
			theDir.mkdir();

			/*
			 * File myfile = new File(theDir, "testfile2.txt"); if (!myfile.createNewFile())
			 * { throw new IOException("Could not create file " + myfile); }
			 * 
			 * git.add().addFilepattern(".").call();
			 * 
			 * git.commit().setMessage("Commit all changes including additions").call();
			 * 
			 * try (PrintWriter writer = new PrintWriter(myfile)) {
			 * writer.append("Hello, world!"); }
			 * 
			 * git.commit().setAll(true).setMessage("Commit changes to all files").call();
			 * // now open the created repository FileRepositoryBuilder builder = new
			 * FileRepositoryBuilder(); try (Repository repository1 =
			 * builder.setGitDir(localPath).readEnvironment().findGitDir().build()) {
			 * 
			 * try (Git git1 = new Git(repository1)) { git1.push().call(); }
			 * System.out.println("Pushed from repository: " + repository1.getDirectory() +
			 * " to remote repository at " + REMOTE_URL); }
			 */
		}

		/*
		 * Path repoPath =
		 * Paths.get("https://github.com/rjgrover05/RealTimeProjects.git"); if
		 * (!Files.exists(repoPath)) { try (Git git =
		 * Git.init().setDirectory(repoPath.toFile()).call()) { } }
		 */

		// try (Git git = Git.open(repoPath.toFile())) {

		/*
		 * Files.writeString(repoPath.resolve("file1.md"), "Hello World 1");
		 * git.add().addFilepattern("file1.md").call();
		 * git.commit().setMessage("create file1").setAuthor("rjgrover05",
		 * "rjkumar.java@gmail.com").call();
		 */

		/*
		 * Files.writeString(repoPath.resolve("file2.md"), "Hello World 2");
		 * git.add().addFilepattern(".").call();
		 * git.commit().setMessage("create file2").setAuthor("author",
		 * "author@email.com") .call();
		 * 
		 * // Update Files.writeString(repoPath.resolve("file1.md"), "Hello Earth 1");
		 * git.commit().setAll(true).setMessage("update file1") .setAuthor("author",
		 * "author@email.com").call();
		 * 
		 * // Delete Files.deleteIfExists(repoPath.resolve("file2.md"));
		 * git.commit().setAll(true).setMessage("delete file2") .setAuthor("author",
		 * "author@email.com").call();
		 * 
		 * // or git.rm().addFilepattern("file2.md").call();
		 * git.commit().setMessage("delete file2").setAuthor("author",
		 * "author@email.com") .call();
		 */

		// }
	}

}
