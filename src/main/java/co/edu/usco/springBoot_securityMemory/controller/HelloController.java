package co.edu.usco.springBoot_securityMemory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/admin/hello")
	public String adminHello() {
		return "Hello Admin!";
	}

	@GetMapping("/user/hello")
	public String userHello() {
		return "Hello User!";
	}
}
