package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

	
	@Autowired
	private TokenGenerationService tokenGenerationService;
	
	@GetMapping()
	public String sayHello() {
		return tokenGenerationService.getCobrandJWT();
	}
}
