package com._Talent._blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com._Talent._blog.dto.Fpassword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Service
public class ProfileService {

    @Autowired
    private UserService userService;

    public void changePassword(Fpassword fpasswordRequest) {
        try {
            userService.updatePassword(fpasswordRequest.getUsername(), fpasswordRequest.getNewPassword());
        } catch (Exception e) {
            throw new RuntimeException("Failed to change password", e);
        }
    }
}
