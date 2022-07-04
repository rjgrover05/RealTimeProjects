package com.rj.git.test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rj.git.test.jgit.JgitAPIsImpl;

@SpringBootApplication
public class GitRestApiExampleApplication implements CommandLineRunner {

	@Autowired
	private JgitAPIsImpl service;
	
	public static void main(String[] args) {
		SpringApplication.run(GitRestApiExampleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//readData();
		// service.cloneGit();
		//service.commitToRepo();
		//service.pushToRepoDefault();
		//service.pushToRepoWithAuth();
		//service.pushToRepoWithSSH();
		//service.cloneAndPush();
	}
	
	public void readData() throws IOException, URISyntaxException {
		/*
		 * Call GitHub REST API - https://developer.github.com/v3/repos/contents/
		 * 
		 * Using Spring's RestTemplate to simplify REST call. Any other REST client
		 * library can be used here.
		 */
		RestTemplate restTemplate = new RestTemplate();
		List<Map> response = restTemplate.getForObject(
				"https://api.github.com/repos/{owner}/{repo}/contents/spring-jdbc/src/main/resources?ref={branch}", List.class, "rjgrover05",
				"RealTimeProjects", "main");

		System.out.println("Response: "+response);
		
		// To print response JSON, using GSON. Any other JSON parser can be used here.
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println("<JSON RESPONSE START>\n" + gson.toJson(response) + "\n<JSON RESPONSE END>\n");

		// Iterate through list of file metadata from response.
		for (Map fileMetaData : response) {

			System.out.println("Map: " + fileMetaData);

			// Get file name & raw file download URL from response.
			String fileName = (String) fileMetaData.get("name");
			String downloadUrl = (String) fileMetaData.get("download_url");
			System.out.println("File Name = " + fileName + " | Download URL = " + downloadUrl);

			// We will only fetch read me file for this example.
			if (downloadUrl != null) {
				downloadUrl = "https://raw.githubusercontent.com/rjgrover05/RealTimeProjects/main/spring-jdbc/src/main/resources/application.properties";
				fileName ="application.properties";
				/*
				 * Get file content as string
				 * 
				 * Using Apache commons IO to read content from the remote URL. Any other HTTP
				 * client library can be used here.
				 */
				String fileContent = IOUtils.toString(new URI(downloadUrl), Charset.defaultCharset());
				System.out.println("\nfileContent = <FILE CONTENT START>\n" + fileContent + "\n<FILE CONTENT END>\n");

				/*
				 * Download read me file to local.
				 * 
				 * Using Apache commons IO to create file from remote content. Any other library
				 * or code can be written to get content from URL & create file in local.
				 */
				File file = new File("github-api-downloaded-" + fileName);
				FileUtils.copyURLToFile(new URL(downloadUrl), file);
			}
		}
	}
}
