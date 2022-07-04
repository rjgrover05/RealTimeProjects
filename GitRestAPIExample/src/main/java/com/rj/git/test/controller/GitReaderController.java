package com.rj.git.test.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rj.git.test.service.GitReaderService;

@RestController
public class GitReaderController {

	@Autowired
	private GitReaderService service;
	
	@PostMapping("/readData")
	public String gitRequest(@RequestBody RequestModel model,HttpServletRequest request) throws IOException, URISyntaxException {
		System.out.println(model);
		return service.read(model,request);
	}
	
	@PostMapping("/withToken")
	public String gitRequestWithToken(@RequestBody RequestModel model) throws IOException, URISyntaxException {
		System.out.println(model);
		return service.readWithToken(model);
	}
	
}
