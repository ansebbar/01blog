package com._Talent._blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

// import com._Talent._blog.controllers.AuthController;
// import com._Talent._blog.controllers.Register;
// import com._Talent._blog.model.Entity.User;
import com._Talent._blog.services.AuthService;

@SpringBootApplication
// (scanBasePackages ="java.com._Talent._blog.services")//this make it easier for the the dependecie injection
public class Application {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(Application.class, args);
		AuthService authService = context.getBean(AuthService.class);
		authService.registeradmin();
	}

}
