package com._Talent._blog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com._Talent._blog.dto.Fpassword;
import com._Talent._blog.model.Entity.Report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com._Talent._blog.model.Entity.User;
import com._Talent._blog.repositery.ReportsRepository;
import com._Talent._blog.repositery.UserRepository;
import com._Talent._blog.services.UserService;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Service
public class ProfileService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userrepo;
    @Autowired
    private ReportsRepository reportsRepository;

    public void changePassword(Fpassword fpasswordRequest) {
        try {
            userService.updatePassword(fpasswordRequest.getUsername(), fpasswordRequest.getNewPassword());
        } catch (Exception e) {
            throw new RuntimeException("Failed to change password", e);
        }
    }


public void reportUser(String reporterUsername, User reportedUsername, String reason) { 
    if (reportedUsername == null) {
        throw new RuntimeException("Reported user not found");
    }
    
    Report report = new Report();
    report.setFromUser(reporterUsername);
    report.setUser(reportedUsername);
    report.setReason(reason);
    report.setType("USER");
    // report.setContentId(reportedUsername.getId());
    reportedUsername.addReport(report);
    userrepo.save(reportedUsername);
    reportsRepository.save(report);
}
}