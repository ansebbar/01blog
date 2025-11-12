package com._Talent._blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// (scanBasePackages ="java.com._Talent._blog.services")//this make it easier for the the dependecie injection
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
