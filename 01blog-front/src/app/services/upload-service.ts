import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UploadResponse {
  url: string;           // Main URL to use
  publicId: string;      // Cloudinary public ID
  type: string;          // "image", "video", or "raw"
  fileName: string;      // Original filename
  thumbnailUrl?: string; // For videos only
  width?: number;
  height?: number;
  duration?: number;
  format?: string;
  resourceType: 'image' | 'video' | 'raw';
  secureUrl: string;
  originalFilename: string;
}

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  private apiUrl = 'http://localhost:8080/api/upload';

  constructor(private http: HttpClient) { }

  // Upload single file
  uploadFile(file: File, folder?: string): Observable<UploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    
    if (folder) {
      formData.append('folder', folder);
    }

    return this.http.post<UploadResponse>(`${this.apiUrl}/file`, formData);
  }

  uploadAvatar(file: File, username: string): Observable<UploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('folder', 'avatars');
    formData.append('username', username);

    return this.http.post<UploadResponse>(`${this.apiUrl}/avatar`, formData);
  }

  // Delete file
  deleteFile(publicId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${publicId}`);
  }

  // Helper methods
  getFileIcon(type: string): string {
    if (type === 'image') return 'image';
    if (type === 'video') return 'movie';
    if (type === 'pdf') return 'picture_as_pdf';
    if (type === 'word') return 'description';
    if (type === 'text') return 'text_snippet';
    return 'insert_drive_file';
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  isImage(file: File): boolean {
    return file.type.startsWith('image/');
  }

  isVideo(file: File): boolean {
    return file.type.startsWith('video/');
  }
}