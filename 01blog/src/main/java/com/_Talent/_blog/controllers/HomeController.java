package com._Talent._blog.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${app.frontend.url}")
public class HomeController {
    
    @GetMapping("/home")
    public String home() {
        return "Welcome to the Blog API!";
    }
    
    @GetMapping("/public/hello")
    public String publicHello() {
        return "This is a public endpoint!";
    }
    
    @GetMapping("/secure/hello")
    public String secureHello() {
        return "This is a secure endpoint!";
    }
}