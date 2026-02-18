package com._Talent._blog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import com._Talent._blog.config.JwtAuthFilter;
import com._Talent._blog.config.JwtUtil;
import com._Talent._blog.dto.UserinfoRequest;
import com._Talent._blog.model.Entity.User;
import com._Talent._blog.repositery.UserRepository;
import com._Talent._blog.services.ProfileService;
import com._Talent._blog.services.UserService;

import lombok.Data;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com._Talent._blog.model.Entity.UserFollowing;
import com._Talent._blog.repositery.UserFollowingRepository;
import com._Talent._blog.dto.ReportRequest;
import com._Talent._blog.model.Entity.Report;

@RestController
@Data
@RequestMapping("/api")
@CrossOrigin(origins = "${app.frontend.url}")
public class ProfileController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserFollowingRepository userFollowingRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProfileService profileservice;

    @GetMapping("/myprofile")
    public UserinfoRequest getMyProfile() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            // System.out.println("Current authenticated username:ffffffffffffffffffffffffffffffffffffffffffff " + username);
            if (username == null || username.equals("anonymousUser")) {
                throw new RuntimeException("User not authenticated");
            }
            return userService.getUserByUsername(username);
        } catch (Exception e) {
            throw new RuntimeException("User not found or not authenticated");
        }
    }

    @GetMapping("/allusers")
    public UserinfoRequest[] getAllUsers(@RequestParam("userid") String userid) {
        try {
            Long id = Long.parseLong(userid);
            return userService.getAllUsers(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch users");
        }
    }

    // Get public profile by username
    @GetMapping("/profile")
    public UserinfoRequest getUserProfile(@RequestParam ("username") String username,@RequestParam("currentuser") String currentuser) {
        try {
            return userService.getPublicUserByUsername(username , currentuser);
        } catch (Exception e) {
            throw new RuntimeException("User not found");
        }
    }

    // Get public profile by ID
    // @GetMapping("/profile/{id}")
    // public UserinfoRequest getUserProfileById(@PathVariable Long id) {
    //     try {
    //         return userService.getPublicUserById(id);
    //     } catch (Exception e) {
    //         throw new RuntimeException("User not found");
    //     }
    // }

    @PutMapping("/updateuserinfo")
    public UserinfoRequest updateUserProfile(@RequestBody UserinfoRequest updatedUser) {
        System.out.println("Received update for user: " + updatedUser.getUsername());
        try {
            // Verify the user is updating their own profile
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            User currUser = userRepository.getUserByUsername(currentUsername).get();

            if (currUser.getEmail().equals(updatedUser.getEmail()) == false){
                if (userRepository.existsByEmail(updatedUser.getEmail())) {
                    throw new RuntimeException("this email is already taken");
                }
            }
            userService.updateUserProfile(updatedUser, currentUsername);
            UserinfoRequest rtn = userService.getUserByUsername(currentUsername);
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                userService.updatePassword(currentUsername, passwordEncoder.encode(updatedUser.getPassword()));
            }
            return rtn;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user profile: " + e.getMessage());
        }
    }
    @PutMapping("/profile/avatar")
    public void updateProfileAvatar(@RequestParam("username") String username, @RequestParam("avatarUrl") String avatarUrl) {
        try {
            User existingUser = userRepository.findByUsername(username).get();
            if (existingUser == null) {
                throw new RuntimeException("User not found with username: " + username);
            }
            System.out.println("Updating avatar for user: " + username + " to URL: " + avatarUrl);
            existingUser.setProfilePicture(avatarUrl);
            userRepository.save(existingUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update profile avatar", e);
        }
    }


    @PostMapping("/profile/togglefollow")
    public void togglefollow(@RequestParam("follower") String follower,@RequestParam("following") String following) {
        try {
            
            User ufollower = userRepository.findByUsername(follower).get();
            User ufollowing = userRepository.findByUsername(following).get();
            if (ufollower.isFollowing(ufollowing)) {
                ufollower.unfollow(ufollowing);
            } else {
                ufollower.follow(ufollowing);
            }
            userRepository.save(ufollower);
            userRepository.save(ufollowing);    
        }catch (Exception e) {
            throw new RuntimeException("Failed to toggle follow", e);
        }
    }
    

    @PostMapping("/profile/report") public void reportUser(@RequestBody ReportRequest reportRequest) {
         try 
         { 
            User reportedUser = userRepository.findByUsername(reportRequest.getReportedusername()).orElseThrow(() -> new RuntimeException("Reported user not found"));
            profileservice.reportUser(reportRequest.getUsername(), reportedUser, reportRequest.getRaison());
            } catch (Exception e) {
                throw new RuntimeException("Failed to report user: " + e.getMessage());
            }


        }
}