package com.rj.controller;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.rj.sp.StoredProcedureForGrant;

@RestController
public class TestController {

	@Autowired
	private StoredProcedureForGrant storedProcedure;

	@GetMapping("data/{username}")
	public void getData(@PathVariable("username") String username) {
		System.out.println(username);
		storedProcedure.executeProcedure_1(username);
	}

}
