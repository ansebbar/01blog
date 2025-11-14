package com._Talent._blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// import com._Talent._blog.model.*;
import com._Talent._blog.repositery.*;
import com._Talent._blog.dto.UserinfoRequest;
import com._Talent._blog.model.Entity.*;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return user;
    }
    
    public UserinfoRequest getUserByUsername(String username)  {
        User usrreq = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        UserinfoRequest userinfo = new UserinfoRequest();
        userinfo.setId(usrreq.getId());
        userinfo.setUsername(usrreq.getUsername());
        userinfo.setEmail(usrreq.getEmail());
        userinfo.setFirstName(usrreq.getFirstName());
        userinfo.setLastName(usrreq.getLastName());
        userinfo.setBio(usrreq.getBio());
        userinfo.setAvatarUrl(usrreq.getProfilePicture());
        return userinfo;
    }


    public User updateUserProfile(User updatedUser) {
        try {
            User existingUser = userRepository.findByUsername(updatedUser.getUsername()).get();
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setBio(updatedUser.getBio());
            existingUser.setProfilePicture(updatedUser.getProfilePicture());
            existingUser.setEmail(updatedUser.getEmail());
            // existingUser.setPassword(authService.passwordEncoder.encode(updatedUser.getPassword()));
            existingUser.setUsername(updatedUser.getUsername());
            // return userRepository.update(existingUser).get();
            return userRepository.save(existingUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user profile", e);
        }
    }
}
