package com._Talent._blog.services;

import com._Talent._blog.dto.UserinfoRequest;
import com._Talent._blog.model.Entity.User;
import com._Talent._blog.model.Entity.UserFollowing;
import com._Talent._blog.repositery.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._Talent._blog.services.*;
import java.util.List;
import java.util.Random;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FPService fpService;

    @Autowired
    private PostService postservice;

    // @Autowired
    // private AuthService authService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return user;
    }
    
    public UserinfoRequest getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return convertToUserinfoRequest(user, 1); 
    }
    
    public UserinfoRequest getPublicUserByUsername(String username , String currentusername) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        User currentUser = userRepository.findByUsername(currentusername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + currentusername));
        if (username.equals(currentusername)) {
            return convertToUserinfoRequest(user, 1);
        }else if (user.getFollowers().contains(currentUser)) {
            return convertToUserinfoRequest(user, 2);
        }
        return convertToUserinfoRequest(user, 0);
    }
    
    // Convert User entity to UserinfoRequest DTO
    private UserinfoRequest convertToUserinfoRequest(User user, int pvinfo) {
        UserinfoRequest userinfo = new UserinfoRequest();
        
        // Basic info
        userinfo.setId(user.getId());
        userinfo.setUsername(user.getUsername());
        userinfo.setFirstName(user.getFirstName());
        userinfo.setLastName(user.getLastName());
        userinfo.setBio(user.getBio());
        userinfo.setAvatarUrl(user.getProfilePicture());
        userinfo.setStatus(user.getStatus());
        userinfo.setRateadmin(user.getRateadmin());
        
        // Stats
        userinfo.setPostsCount(user.getPostCount());
        userinfo.setFollowersCount(user.getFollowerCount());
        userinfo.setFollowingCount(user.getFollowingCount());

        userinfo.setPosts(postservice.getpostsofany(user.getPosts()));
            userinfo.setCreatedAt(user.getCreatedAt().toString());
        if (pvinfo == 1 || pvinfo == 2) {
            if (pvinfo == 1){
                userinfo.setEmail(user.getEmail());
            }
            
            userinfo.setFollowers(new UserinfoRequest[user.getFollowerRelations().size()]);
            userinfo.setFollowing(new UserinfoRequest[user.getFollowingRelations().size()]);
            // userinfo.setEmail(user.getEmail());
            for( User uf : user.getFollowerRelations().stream().map(UserFollowing::getFollower).toList()) {
                // System.out.println("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
                UserinfoRequest followerInfo = new UserinfoRequest();
                followerInfo.setId(uf.getId());
                followerInfo.setUsername(uf.getUsername());
                followerInfo.setFirstName(uf.getFirstName());
                followerInfo.setLastName(uf.getLastName());
                followerInfo.setAvatarUrl(uf.getProfilePicture());
                followerInfo.setFollowersCount(uf.getFollowerCount());
                followerInfo.setPostsCount(uf.getPostCount());
                userinfo.getFollowers()[userinfo.getFollowersCount()-1] = followerInfo;
            }
            for( User uf : user.getFollowingRelations().stream().map(UserFollowing::getFollowing).toList()) {
                UserinfoRequest followingInfo = new UserinfoRequest();
                followingInfo.setId(uf.getId());
                followingInfo.setUsername(uf.getUsername());
                followingInfo.setFirstName(uf.getFirstName());
                followingInfo.setLastName(uf.getLastName());
                followingInfo.setAvatarUrl(uf.getProfilePicture());
                followingInfo.setFollowersCount(uf.getFollowerCount());
                followingInfo.setPostsCount(uf.getPostCount());
                userinfo.getFollowing()[userinfo.getFollowingCount()-1] = followingInfo;
            }

        }
        
        return userinfo;
    }

    public UserinfoRequest[] getAllUsers(Long iduser) {
        List<User> users = userRepository.findAll();
        int sizeofadmin = (int) users.stream().filter(u -> u.getRateadmin().equals("admin")).count();
        User currentUser = userRepository.findById(iduser)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + iduser));
        int arrsize = users.size() - sizeofadmin - 1;
        if(currentUser.getRateadmin().equals("superadmin")){
            arrsize = users.size() - 1;
        }
        UserinfoRequest[] userDTOs = new UserinfoRequest[arrsize];
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getRateadmin().equals("superadmin") || users.get(i).getUsername().equals(currentUser.getUsername())) {
                continue; 
            }else if (users.get(i).getRateadmin().equals("admin")) {
                if (!currentUser.getRateadmin().equals("superadmin")) {
                    continue;
                }
            }
            User user = users.get(i);
            UserinfoRequest dto = new UserinfoRequest();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setAvatarUrl(user.getProfilePicture());
            dto.setStatus(user.getStatus());
            if(currentUser.getRateadmin().equals("superadmin")) {
                dto.setRateadmin(user.getRateadmin());
            }
            userDTOs[i] = dto;
        }
        return userDTOs;
    }
    
    // Get followers as UserinfoRequest array
    public UserinfoRequest[] getFollowersInfo(List<UserFollowing> followers) {
        UserinfoRequest[] followersInfo = new UserinfoRequest[followers.size()];
        for (int i = 0; i < followers.size(); i++) {
            User followerUser = followers.get(i).getFollower();
            UserinfoRequest followerInfo = new UserinfoRequest();
            followerInfo.setId(followerUser.getId());
            followerInfo.setUsername(followerUser.getUsername());
            followerInfo.setFirstName(followerUser.getFirstName());
            followerInfo.setLastName(followerUser.getLastName());
            followerInfo.setAvatarUrl(followerUser.getProfilePicture());
            followersInfo[i] = followerInfo;
        }
        return followersInfo;
    }
    
    // Get following as UserinfoRequest array
    public UserinfoRequest[] getFollowingInfo(List<UserFollowing> following) {
        UserinfoRequest[] followingInfo = new UserinfoRequest[following.size()];
        for (int i = 0; i < following.size(); i++) {
            User followingUser = following.get(i).getFollowing();
            UserinfoRequest followInfo = new UserinfoRequest();
            followInfo.setId(followingUser.getId());
            followInfo.setUsername(followingUser.getUsername());
            followInfo.setFirstName(followingUser.getFirstName());
            followInfo.setLastName(followingUser.getLastName());
            followInfo.setAvatarUrl(followingUser.getProfilePicture());
            followingInfo[i] = followInfo;
        }
        return followingInfo;
    }

    public void updateUserProfile(UserinfoRequest updatedUser, String currentUsername) throws RuntimeException {
        try {
            User existingUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Update allowed fields
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setBio(updatedUser.getBio());
            
            if (updatedUser.getAvatarUrl() != null && !updatedUser.getAvatarUrl().isEmpty()) {
                System.out.println("Updating profile picture to: " + updatedUser.getAvatarUrl());
                existingUser.setProfilePicture(updatedUser.getAvatarUrl());
            }
            
            // Email update with validation
            if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
                if (userRepository.existsByEmail(updatedUser.getEmail())) {
                    throw new RuntimeException("Email already taken");
                }
                existingUser.setEmail(updatedUser.getEmail());
            }
            
            userRepository.save(existingUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user profile", e);
        }
    }

    public void updatePassword(String username, String newPassword) throws RuntimeException {
        try {
            User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            existingUser.setPassword(newPassword);
            userRepository.save(existingUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user password", e);
        }
    }
    
    @Transactional
    public boolean resetPassword(String email) {
        User user = userRepository.findByEmail(email)
            .orElse(null);
            
        if (user == null) {
            return false;
        }
        
        String newPin = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setPin(newPin);
        userRepository.save(user);
        
        fpService.sendPasswordResetEmail(email, newPin);
        return true;
    }

    public void clearPin(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPin(null);
        userRepository.save(user);
    }

    public boolean checkpin(String email, String pin) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if pin matches
        return pin != null && pin.equals(user.getPin());
    }
    
    // Get user by token
    public User getUserByToken(String token) {
        return userRepository.findByToken(token).orElse(null);
    }

    // In your UserService class
public Long getUserIdByUsername(String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    return user.getId();
}
}