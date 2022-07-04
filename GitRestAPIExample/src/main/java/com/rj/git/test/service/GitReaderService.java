package com.rj.git.test.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rj.git.test.controller.RequestModel;
import com.rj.git.test.model.Student;

@Service
public class GitReaderService {

	@Autowired
	private Reader reader;

	@Autowired
	private Writer writer;

	public String readWithToken(RequestModel model) {
		String url = "https://api.github.com/repos/rjgrover05/Test_Repo?ref=main";
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<>(headers);
		headers.setBearerAuth("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");

		String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();

		System.out.println("Response: " + response);

		return null;
	}

	public String read(RequestModel model,HttpServletRequest request) throws IOException, URISyntaxException {

		RestTemplate restTemplate = new RestTemplate();
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<Map> response = restTemplate.getForObject(
				"https://api.github.com/repos/{owner}/{repo}/contents/spring-jdbc/src/main/resources?ref={branch}",
				List.class, model.getOwner(), model.getRepo(), model.getBranch());

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
				// downloadUrl =
				// "https://raw.githubusercontent.com/rjgrover05/RealTimeProjects/main/spring-jdbc/src/main/resources/student.yml";
				// fileName = "application.properties";

				// reader.readYaml(downloadUrl);

				/*
				 * Get file content as string
				 * 
				 * Using Apache commons IO to read content from the remote URL. Any other HTTP
				 * client library can be used here.
				 */
				if (fileName.equalsIgnoreCase("student.yml")) {

					// reader.readYaml(downloadUrl);
					Student student = reader.readYmlAsBean(downloadUrl);
					System.out.println(student);

					student.setName("Raj Kumar");
					// downloadUrl =
					// "https://raw.githubusercontent.com/rjgrover05/RealTimeProjects/main/spring-jdbc/src/main/resources/";
					// writer.withRestTemplate(downloadUrl, student);
					// writer.uploadSingleFile(student, downloadUrl);

					writer.writeYmlWithBean("C:/Users/raj05/Downloads/GitRestAPIExample/GitRestAPIExample/src/main/resources/", student,request);

					/*
					 * URI uri = new URI(downloadUrl); byte[] input = IOUtils.toByteArray(uri);
					 * System.out.println("Input: "+input); InputStream inputStream = new
					 * ByteArrayInputStream(input); Yaml yaml = new Yaml(); Map<String, Object> data
					 * = yaml.load(inputStream); System.out.println("Yaml Data: " + data);
					 */
				}

				String fileContent = IOUtils.toString(new URI(downloadUrl), Charset.defaultCharset());
				// System.out.println("\nfileContent = <FILE CONTENT START>\n" + fileContent +
				// "\n<FILE CONTENT END>\n");

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

		return null;
	}

}
