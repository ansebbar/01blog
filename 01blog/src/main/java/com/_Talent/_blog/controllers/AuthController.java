package com._Talent._blog.controllers;


import com._Talent._blog.dto.AuthResponse;
// import com._Talent._blog.dto.Fpassword;
import com._Talent._blog.dto.LoginRequest;
import com._Talent._blog.dto.RegisterRequest;
// import com._Talent._blog.model.Entity.User;
import com._Talent._blog.services.AuthService;
// import com._Talent._blog.services.ProfileService;
import com._Talent._blog.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.repository.query.Param;
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
    private UserService userService;

    
    
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

    @PutMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fpassword")
    public ResponseEntity<Boolean> fpassword(@RequestParam("email") String email) {

        if (!userService.resetPassword(email)) {
                return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(true);
    }
    @PostMapping("/verify-pin")
    public ResponseEntity<Boolean> verifyPin(@RequestParam("pin") String pin, @RequestParam("email") String email) {
        boolean isValid = userService.checkpin(email,pin);
        return ResponseEntity.ok(isValid);
    }
    @GetMapping("/validate-token")
    public Boolean validateToken(@RequestParam("username") String username, @RequestParam("token") String token) {
        return authService.validateToken(username, token);
    }
}