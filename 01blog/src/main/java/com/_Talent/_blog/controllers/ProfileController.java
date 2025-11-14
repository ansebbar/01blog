package com._Talent._blog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com._Talent._blog.dto.UserinfoRequest;
import com._Talent._blog.model.Entity.User;
import com._Talent._blog.repositery.UserRepository;
import com._Talent._blog.services.UserService;

import lombok.Data;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@Data
@RequestMapping("/api")
@CrossOrigin(origins = "${app.frontend.url}")
public class ProfileController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public UserinfoRequest getUserProfile(@RequestParam String username) {
        try {
            return userService.getUserByUsername(username);
        } catch (Exception e) {
            throw new RuntimeException("User not found");
        }
    }
    @PutMapping("/updateuserinfo")
    public User updateUserProfile(@RequestBody User updatedUser) {
        try {
            return userService.updateUserProfile(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user profile");
        }
    }



}
