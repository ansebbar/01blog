package com._Talent._blog.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com._Talent._blog.services.SupportService;
import com._Talent._blog.dto.SupportRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com._Talent._blog.dto.ApiResponse;


@RestController
@RequestMapping("/api/support")
@CrossOrigin(origins = "${app.frontend.url}")
public class SuppController {
    @Autowired
    private SupportService supportService;

    @PostMapping("/contact")
    public ResponseEntity<ApiResponse> submitSupportRequest(@RequestBody SupportRequest request) {
        System.out.println("Received support request: " + request);
        supportService.sendSupportEmail(request);
        supportService.sendConfirmationEmail(request.getEmail(), request.getName());
        return ResponseEntity.ok(new ApiResponse(true, "Support request submitted successfully."));
    }

}
