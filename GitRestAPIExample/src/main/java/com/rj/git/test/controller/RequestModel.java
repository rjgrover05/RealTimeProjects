package com.rj.git.test.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestModel {

	private String owner;
	private String repo;
	private String branch;

}
