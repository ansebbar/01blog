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
import com._Talent._blog.model.Entity.*;
import java.util.Optional;


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

    public String LoginChecker(User usr){
        Optional<User> us = userRepository.findByEmail(usr.getEmail());
        if(us.isEmpty())
             us = userRepository.findByUsername(usr.getUsername());
        if(us.isEmpty())
            return "email or username wrong";
        else if (us.get().getPassword() != usr.getPassword())
            return "password mistaken";
        return "welcome";
        // return lgm.checkuser(usr) == true ? "user exist" : "not found";        
    }

    public String gettmp(){
        return "lOGIN";
    }

}
