package com.rj.git.test.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.rj.git.test.GitRestApiExampleApplication;
import com.rj.git.test.model.Course;
import com.rj.git.test.model.Student;

@Service
public class Writer {

	@Autowired
	private ResourceLoader rLoader;
	
	public void WriteYaml() throws IOException {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("id", 19);
		dataMap.put("name", "John");
		dataMap.put("address", "Star City");
		dataMap.put("department", "Medical");

		DumperOptions options = new DumperOptions();
		options.setIndent(2);
		options.setPrettyFlow(true);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(options);
		PrintWriter writer = new PrintWriter(new File("./src/main/resources/student_output.yml"));
//        StringWriter writer = new StringWriter();
		yaml.dump(dataMap, writer);
	}

	public void WriteYamlBasic() {

		DumperOptions options = new DumperOptions();
		options.setIndent(2);
		options.setPrettyFlow(true);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(options);

		StringWriter writer = new StringWriter();
		yaml.dump(basicStudentObject(), writer);
		System.out.println(writer.toString());
	}

	public void WriteYamlBasicWithCollection() throws FileNotFoundException {

		DumperOptions options = new DumperOptions();
		options.setIndent(2);
		options.setPrettyFlow(true);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(options);

//        StringWriter writer = new StringWriter();
		PrintWriter writer = new PrintWriter(new File("./src/main/resources/student_output_bean.yml"));
		yaml.dump(studentObject(), writer);
		System.out.println(writer.toString());
	}

	private Student basicStudentObject() {
		Student student = new Student();

		student.setId(21);
		student.setName("Tim");
		student.setAddress("Night City");
		student.setYear(2077);
		student.setDepartment("Cyberware");

		return student;
	}

	private Student studentObject() {
		Student student = new Student();

		student.setId(21);
		student.setName("Tim");
		student.setAddress("Night City");
		student.setYear(2077);
		student.setDepartment("Cyberware");

		Course courseOne = new Course();
		courseOne.setName("Intelligence");
		courseOne.setCredits(5);

		Course courseTwo = new Course();
		courseTwo.setName("Crafting");
		courseTwo.setCredits(2);

		List<Course> courseList = new ArrayList<>();
		courseList.add(courseOne);
		courseList.add(courseTwo);

		student.setCourses(courseList);

		return student;
	}

	public Student writeYmlWithBean(String path, Student student, HttpServletRequest request)
			throws IOException, URISyntaxException {
		try {
			DumperOptions options = new DumperOptions();
			options.setIndent(2);
			options.setPrettyFlow(true);
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			Yaml yaml = new Yaml(options);

			String fileName = "student.yml";
			File filePath = new File(path + fileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath.toURI())));
			yaml.dump(student, writer);

			// InputStream inputStream =
			// this.getClass().getClassLoader().getResourceAsStream("student.yml");
			/*Resource resource = new ClassPathResource("classpath:student.yml");
			URL stream = this.getClass().getClassLoader().getResource("student.yml");
			System.out.println(stream.getPath());*/
/*			byte[] b = FileCopyUtils.copyToByteArray(stream);
			String s= new String(b, StandardCharsets.UTF_8);
			System.out.println("Data: "+s);*/
			
			Resource resource = rLoader.getResource("classpath:student.yml");
			
			path = "https://raw.githubusercontent.com/rjgrover05/RealTimeProjects/main/spring-jdbc/src/main/resources/";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			System.out.println("Contextpath: " + resource.getFile().toURI());
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file",resource.getFile().toURI());

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.postForEntity(path, requestEntity, String.class);
			System.out.println("Response code: " + response.getStatusCode());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public ResponseEntity<Student> withRestTemplate(String path, Student student) throws URISyntaxException {
		RestTemplate restTemplate = new RestTemplate();
		URI uri = new URI(path);
		ResponseEntity<Student> result = restTemplate.postForEntity(uri, student, Student.class);
		return result;
	}

	public static Resource getTestFile(Student student) throws IOException {
		Path testFile = Files.createTempFile("student", ".yml");
		System.out.println("Creating and Uploading Test File: " + testFile);
		Files.write(testFile, student.toString().getBytes());
		return new FileSystemResource(testFile.toFile());
	}

	public static void uploadSingleFile(Student student, String path) throws IOException {
		path = "https:///raw.githubusercontent.com/rjgrover05/RealTimeProjects/main/spring-jdbc/src/main/resources/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", getTestFile(student));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		// String serverUrl =
		// "http://localhost:8082/spring-rest/fileserver/singlefileupload/";
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Student> response = restTemplate.postForEntity(path, requestEntity, Student.class);
		System.out.println("Response code: " + response.getStatusCode());
	}
}
