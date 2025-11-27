package com._Talent._blog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import com._Talent._blog.model.Entity.*;
import com._Talent._blog.services.*;;

@RestController
@RequestMapping("/api/users")  // This defines the base path
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular origin
public class UserController {

    // Temporary in-memory storage for testing
    private List<User> users = new ArrayList<>();
    // private Long nextId = 1L;
    @Autowired
    private RegistrationService nwuser;

    public UserController() {
        // Add some test data
        users.add(new User());
        users.add(new User());
    }

    // GET all users
    @GetMapping
    public List<User> getAllUsers() {
        return users;
    }

    // GET user by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // POST create new user
    // @PostMapping
    // public String createUser(@RequestBody User user) {
    //     // users.setId(nextId++);
    //     // System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiihiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");

    //     return nwuser.setuser(user);
    // }

    // PUT update user
    // @PutMapping("/{id}")
    // public Users updateUser(@PathVariable Long id, @RequestBody Users updatedUser) {
    //     Users existingUser = getUserById(id);
    //     if (existingUser != null) {
    //         existingUser.setName(updatedUser.getName());
    //         existingUser.setEmail(updatedUser.getEmail());
    //     }
    //     return existingUser;
    // }

    // DELETE user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        users.removeIf(user -> user.getId().equals(id));
    }
}