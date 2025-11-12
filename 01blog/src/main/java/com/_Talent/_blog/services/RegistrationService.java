package com._Talent._blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com._Talent._blog.model.*;
import java.util.List;
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

    public String setuser(User ur) {
        System.out.println(ur);
        Optional<User> us = this.usrt.findByEmail(ur.getEmail());
        Optional<User> un = this.usrt.findByUsername(ur.getUsername());
        // this.usrt.findById(1);
        if (us.isPresent())
            return "email is already registred";
        if (un.isPresent())
            return "username taken";
        this.usrt.save(ur);
        return "welcome";
    }
}
