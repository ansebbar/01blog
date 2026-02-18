package com._Talent._blog.controllers;

import com._Talent._blog.repositery.UserRepository;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.utils.ObjectUtils;

import java.util.*;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.frontend.url}")
public class UploadController {

    @Autowired
    private UserRepository userRepository;
 
    private final Cloudinary cloudinary;
    
    // @GetMapping("/check-config")
    // public Map<String, Object> checkConfig() {
    //     Map<String, Object> response = new HashMap<>();
    //     try {
    //         response.put("cloudName", cloudinary.config.cloudName);
    //         response.put("apiKeyFirst5", cloudinary.config.apiKey.substring(0, 5));
    //         response.put("injected", true);
    //         response.put("success", true);
    //     } catch (Exception e) {
    //         response.put("injected", false);
    //         response.put("error", e.getMessage());
    //         response.put("success", false);
    //     }
    //     return response;
    // }

    @PostMapping("/avatar")
    public Map<String, Object> uploadAvatar(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "username", required = false) String username) {
        
        System.out.println("=== AVATAR UPLOAD START ===");
        System.out.println("📤 File: " + file.getOriginalFilename());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // System.out.println("📏 Size: " + file.getSize() + " bytes");
            // System.out.println("📄 Type: " + file.getContentType());
            
            byte[] fileBytes = file.getBytes();
            // System.out.println("✅ Got file bytes: " + fileBytes.length + " bytes");
            
            Map<String, Object> simpleOptions = new HashMap<>();
            simpleOptions.put("folder", "avatar-uploads");
            simpleOptions.put("resource_type", "image");
            
            // System.out.println("🚀 Uploading to Cloudinary...");
            // System.out.println("Cloud Name: " + cloudinary.config.cloudName);
            
            Map uploadResult = cloudinary.uploader().upload(fileBytes, simpleOptions);
            // System.out.println("✅ UPLOAD SUCCESSFUL!");
            
            String originalUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            
            // System.out.println("🔗 URL: " + originalUrl);
            
            // Generate transformed URL
            // String transformedUrl = cloudinary.url()
            //     .resourceType("image")
            //     .transformation("c_fill,w_200,h_200,g_face/r_max")
            //     .secure(true)
            //     .generate(publicId);
            
            // System.out.println("🎨 Transformed URL: " + transformedUrl);
            
            // Update user if provided
            if (username != null && !username.trim().isEmpty()) {
                userRepository.findByUsername(username).ifPresent(user -> {
                    user.setProfilePicture(originalUrl);
                    userRepository.save(user);
                    System.out.println("✅ Updated profile for: " + username);
                });
            }
            
            response.put("success", true);
            response.put("url", originalUrl);
            response.put("publicId", publicId);
            response.put("type", "image");
            response.put("fileName", file.getOriginalFilename());
            response.put("message", "Avatar uploaded successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("error", "Upload failed: " + e.getMessage());
            response.put("detail", e.getClass().getName());
        }
        
        return response;
    }
       

    @PostMapping("/file")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== FILE UPLOAD START ===");
            
            Map<String, Object> options = new HashMap<>();
            String contentType = file.getContentType();
            
            if (contentType != null && contentType.startsWith("image/")) {
                options.put("resource_type", "image");
                options.put("quality", "auto:good");
            } else if (contentType != null && contentType.startsWith("video/")) {
                options.put("resource_type", "video");
            } else {
                options.put("resource_type", "raw");
            }
            
            System.out.println("Uploading to Cloudinary...");
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            System.out.println("✅ FILE UPLOAD SUCCESSFUL!");
            
            response.put("success", true);
            response.put("url", uploadResult.get("secure_url"));
            response.put("publicId", uploadResult.get("public_id"));
            response.put("type", uploadResult.get("resource_type"));
            response.put("fileName", file.getOriginalFilename());
            
        } catch (Exception e) {
            System.err.println("❌ File upload failed: " + e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    @DeleteMapping("/delete/{publicId}")
    public Map<String, Object> deleteFile(@PathVariable String publicId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            response.put("success", true);
            response.put("result", result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
}