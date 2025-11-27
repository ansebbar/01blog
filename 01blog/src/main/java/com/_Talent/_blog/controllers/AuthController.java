package com._Talent._blog.controllers;


import com._Talent._blog.dto.AuthResponse;
import com._Talent._blog.dto.Fpassword;
import com._Talent._blog.dto.LoginRequest;
import com._Talent._blog.dto.RegisterRequest;
import com._Talent._blog.services.AuthService;
import com._Talent._blog.services.ProfileService;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${app.frontend.url}")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private ProfileService profileService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/fpassword")
    public ResponseEntity<Integer> fpassword(@RequestBody Fpassword fpasswordRequest) {
        try {
            this.profileService.changePassword(fpasswordRequest);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(0);
        }
        return ResponseEntity.ok(1);
    }


}