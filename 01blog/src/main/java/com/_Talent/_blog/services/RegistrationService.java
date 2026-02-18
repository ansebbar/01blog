package com._Talent._blog.services;

import org.springframework.stereotype.Service;
import com._Talent._blog.repositery.*;
import java.util.Optional;
import com._Talent._blog.model.Entity.*;


@Service
public class RegistrationService {

    // @Autowired
    // private UsersModel usr;

    // @Autowired
    private UserRepository usrt;

    public RegistrationService(UserRepository us ){
        this.usrt  = us;
        // this.usr = usrm;
    }

    public String[] setuser(User ur) {
        System.out.println(ur);
        Optional<User> us = this.usrt.findByEmail(ur.getEmail());
        Optional<User> un = this.usrt.findByUsername(ur.getUsername());
        // this.usrt.findById(1);
        
        String[] responses = new String[10];
        if (us.isPresent())
            responses[0] = "email is already registred";
        if (un.isPresent())
            responses[1] = "username taken";
        if (ur.getPassword().length() < 6)
            responses[2] = "password must be at least 6 characters";
        if (ur.getUsername().length() < 3)
            responses[3] = "username must be at least 3 characters";
        if (ur.getEmail().length() < 5 || !ur.getEmail().contains("@"))
            responses[4] = "invalid email address";
        if (ur.getFirstName().length() < 1)
            responses[5] = "first name required";
        if (ur.getLastName().length() < 1)
            responses[6] = "last name required";
        if (ur.getPassword().length() > 20)
            responses[7] = "password must be less than 20 characters";
        if (ur.getUsername().length() > 15)
            responses[8] = "username must be less than 15 characters";
        if (ur.getEmail().length() > 50)
            responses[9] =  "email must be less than 50 characters";
        this.usrt.save(ur);
        return responses;
    }
}
