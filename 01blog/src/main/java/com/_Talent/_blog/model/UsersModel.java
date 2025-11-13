package com._Talent._blog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
// import java.util.UUID;
import com._Talent._blog.model.Entity.*;

import org.springframework.stereotype.Component;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UsersModel {
    private List<User> user = new ArrayList<>();

    public void addusr(User usr) {
        // usr.setUi(UUID.randomUUID());
        user.add(usr);
    } 

    public boolean checkuser(User usr) {
        System.out.println(user);
        return user.stream().anyMatch(u-> u.getUsername().equals(usr.getUsername()));
    }

    public List<User> getuser() {
        System.out.println(user);
        return user;
    }
}
