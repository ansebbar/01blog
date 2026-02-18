// package com._Talent._blog.services;
// import com.cloudinary.Cloudinary;
// import com.cloudinary.utils.ObjectUtils;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.util.HashMap;
// import java.util.Map;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class CloudinaryService {
    
//     private final Cloudinary cloudinary;
    
//     public Map<String, Object> uploadFile(MultipartFile file, String folder) throws IOException {
//         try {
//             File convertedFile = convertMultiPartToFile(file);
            
//             Map<String, Object> uploadOptions = ObjectUtils.asMap(
//                 "folder", folder,
//                 "resource_type", "auto", // Auto-detect image/video
//                 "public_id", file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.')),
//                 "overwrite", true,
//                 "notification_url", null,
//                 "unique_filename", false
//             );
            
//             // Upload to Cloudinary
//             Map<String, Object> uploadResult = cloudinary.uploader().upload(convertedFile, uploadOptions);
            
//             // Delete temporary file
//             convertedFile.delete();
            
//             log.info("File uploaded successfully: {}", uploadResult.get("url"));
//             return uploadResult;
            
//         } catch (Exception e) {
//             log.error("Error uploading file to Cloudinary: {}", e.getMessage());
//             throw new IOException("Failed to upload file to Cloudinary", e);
//         }
//     }
    
//     public Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException {
//         try {
//             File convertedFile = convertMultiPartToFile(file);
            
//             Map<String, Object> uploadOptions = ObjectUtils.asMap(
//                 "folder", folder + "/images",
//                 "transformation", new Object[] {
//                     ObjectUtils.asMap("width", 1200, "height", 800, "crop", "limit"),
//                     ObjectUtils.asMap("quality", "auto:good"),
//                     ObjectUtils.asMap("fetch_format", "auto")
//                 }
//             );
            
//             Map<String, Object> uploadResult = cloudinary.uploader().upload(convertedFile, uploadOptions);
//             convertedFile.delete();
            
//             return uploadResult;
            
//         } catch (Exception e) {
//             throw new IOException("Failed to upload image", e);
//         }
//     }
    
//     public Map<String, Object> uploadVideo(MultipartFile file, String folder) throws IOException {
//         try {
//             File convertedFile = convertMultiPartToFile(file);
            
//             Map<String, Object> uploadOptions = ObjectUtils.asMap(
//                 "folder", folder + "/videos",
//                 "resource_type", "video",
//                 "chunk_size", 6000000, // 6MB chunks for large videos
//                 "eager", new Object[] {
//                     ObjectUtils.asMap("width", 800, "height", 600, "crop", "scale")
//                 },
//                 "eager_async", true
//             );
            
//             Map<String, Object> uploadResult = cloudinary.uploader().upload(convertedFile, uploadOptions);
//             convertedFile.delete();
            
//             return uploadResult;
            
//         } catch (Exception e) {
//             throw new IOException("Failed to upload video", e);
//         }
//     }
    
//     public void deleteFile(String publicId) throws IOException {
//         try {
//             Map<String, Object> deleteOptions = ObjectUtils.asMap(
//                 "resource_type", "image" // or "video" or "raw" or "auto"
//             );
            
//             Map<String, Object> result = cloudinary.uploader().destroy(publicId, deleteOptions);
//             log.info("File deleted: {}", result);
            
//         } catch (Exception e) {
//             log.error("Error deleting file from Cloudinary: {}", e.getMessage());
//             throw new IOException("Failed to delete file from Cloudinary", e);
//         }
//     }
    
//     private File convertMultiPartToFile(MultipartFile file) throws IOException {
//         File convertedFile = new File(file.getOriginalFilename());
//         try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
//             fos.write(file.getBytes());
//         }
//         return convertedFile;
//     }
    
//     public String getFileUrl(String publicId, String resourceType) {
//         if (resourceType.equals("video")) {
//             return cloudinary.url().resourceType(resourceType).generate(publicId);
//         }
//         return cloudinary.url().generate(publicId);
//     }
    
//     public String getThumbnailUrl(String publicId, String resourceType, int width, int height) {
//         if (resourceType.equals("video")) {
//             return cloudinary.url()
//                 .resourceType(resourceType)
//                 .transformation(new com.cloudinary.Transformation()
//                     .width(width)
//                     .height(height)
//                     .crop("fill")
//                     .quality("auto"))
//                 .generate(publicId);
//         }
        
//         return cloudinary.url()
//             .transformation(new com.cloudinary.Transformation()
//                 .width(width)
//                 .height(height)
//                 .crop("fill")
//                 .quality("auto"))
//             .generate(publicId);
//     }
// }