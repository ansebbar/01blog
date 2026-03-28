package com._Talent._blog.services;

import com._Talent._blog.dto.AuthResponse;
import com._Talent._blog.dto.LoginRequest;
import com._Talent._blog.dto.RegisterRequest;
import com._Talent._blog.model.Entity.User;
import com._Talent._blog.repositery.UserRepository;
import com._Talent._blog.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    public PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;


    public boolean registeradmin() {
         User user = new User();
        // user.setEmail(registerRequest.getEmail());
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setEmail("admin@gmail.com");
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setBio("admin");
        user.setRateadmin("superadmin");
        
        // if (registerRequest.getAvatarUrl() != null && !registerRequest.getAvatarUrl().isEmpty()) {
            // user.setProfilePicture("");
        // } else {
            user.setProfilePicture("https://ui-avatars.com/api/?name=" + 
                user.getFirstName() + "+" + user.getLastName() + "&background=4f46e5&color=fff");
        // }
        
        // Initialize other fields
        user.setPin(null); // Initialize pin as null
        user.setToken(null); // Initialize token as null
        
        userRepository.save(user);
        System.out.println("admin registered: " + user.getUsername());
        return true;
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setBio(registerRequest.getBio());
        
        if (registerRequest.getAvatarUrl() != null && !registerRequest.getAvatarUrl().isEmpty()) {
            user.setProfilePicture(registerRequest.getAvatarUrl());
        } else {
            user.setProfilePicture("https://ui-avatars.com/api/?name=" + 
                user.getFirstName() + "+" + user.getLastName() + "&background=4f46e5&color=fff");
        }
        
        // Initialize other fields
        user.setPin(null); // Initialize pin as null
        user.setToken(null); // Initialize token as null
        
        userRepository.save(user);
        System.out.println("User registered: " + user.getUsername());
        return new AuthResponse();
    }
    
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.TkGenerate(user);
        
        // Save token to user
        user.setToken(token);
        userRepository.save(user);

        return new AuthResponse(token, user.getUsername(), user.getEmail(),user.getId().toString(), user.getProfilePicture());
    }
    
    public boolean validateToken(String username, String token) {
        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user == null) {
            System.err.println("User not found: " + username);
            return false;
        }
        
        // Check if token matches and is valid
        if (token == null || !token.equals(user.getToken())) {
            System.err.println("Token mismatch for user: " + username);
            return false;
        }
        
        // Validate with JWT util
        boolean isValid = jwtUtil.TkCheck(token, user);
        System.out.println("Token validation result for user " + username + ": " + isValid);
        
        return isValid;
    }

    public void logout(String token) {
        User user = userRepository.findByToken(token).orElse(null);
        if (user != null) {
            user.setToken(null);
            userRepository.save(user);
        }
    }
    
    // Helper method to get user by token
    public User getUserByToken(String token) {
        return userRepository.findByToken(token).orElse(null);
    }
}