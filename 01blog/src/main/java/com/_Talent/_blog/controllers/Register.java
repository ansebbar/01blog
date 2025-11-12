package com._Talent._blog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._Talent._blog.model.*;
import com._Talent._blog.services.RegistrationService;
import com._Talent._blog.model.Entity.*;

@RestController
@RequestMapping("/Register")
public class Register {

    @Autowired
    private RegistrationService Rsv;

    @PostMapping("submitnewUser")
    public ResponseEntity<String> Registering(@RequestBody User newusr) {
        return ResponseEntity.ok(Rsv.setuser(newusr));
    }

}
