package com.rj.git.test.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.rj.git.test.model.Student;

@Service
public class Reader {

	public void readYaml(String downloadUrl) throws URISyntaxException, IOException {

		URI uri = new URI(downloadUrl);
		byte[] input = IOUtils.toByteArray(uri);
		System.out.println("Input: " + input);
		InputStream inputStream = new ByteArrayInputStream(input);
		Yaml yaml = new Yaml();
		Map<String, Object> data = yaml.load(inputStream);
		System.out.println("Yaml Data: " + data);
	}

	public void readYamlWithCollection() {
		Yaml yaml = new Yaml();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("student_with_courses.yml");
		Map<String, Object> data = yaml.load(inputStream);
		System.out.println(data);
	}

	public Student readYmlAsBean(String downloadUrl) throws URISyntaxException, IOException {
		Yaml yaml = new Yaml(new Constructor(Student.class));
		URI uri = new URI(downloadUrl);
		byte[] input = IOUtils.toByteArray(uri);
		System.out.println("Input: " + input);
		InputStream inputStream = new ByteArrayInputStream(input);
		Student data = yaml.load(inputStream);
		System.out.println("Data: "+data);
		return data;
	}

	public void ReadYamlAsBean() {
		Yaml yaml = new Yaml(new Constructor(Student.class));
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("student.yml");
		Student data = yaml.load(inputStream);
		System.out.println(data);
	}

	public void ReadYamlAsBeanWithNestedClass() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("student_with_courses.yml");
		Yaml yaml = new Yaml(new Constructor(Student.class));
		Student data = yaml.load(inputStream);
		System.out.println(data);
	}
}
