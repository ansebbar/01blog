import { Component, OnInit, ViewChild, ElementRef, HostListener } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms'; 
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { DecimalPipe } from '@angular/common';
import { UploadService, UploadResponse } from '../../services/upload-service';
import { PostService } from '../../services/post-service';

interface MediaPreview {
  id: number;
  url: string;
  file: File;
  type: 'image' | 'video';
  caption: string;
  cloudinaryData?: UploadResponse;
}

@Component({
  selector: 'app-post-creation',
  templateUrl: './post-creation.html',
  styleUrls: ['./post-creation.css'],
  imports: [ReactiveFormsModule, FormsModule, DecimalPipe]
})
export class PostCreateComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  @ViewChild('contentTextarea') contentTextarea!: ElementRef<HTMLTextAreaElement>;

  postForm: FormGroup;
  isSubmitting = false;
  editorMode: 'write' | 'preview' = 'write';
  mediaPreviews: MediaPreview[] = [];
  creatorUsername: string = localStorage.getItem('username') || '';
  
  // Upload tracking
  uploadingFiles: { [key: string]: boolean } = {};
  uploadProgress: { [key: string]: number } = {};
  uploadErrors: { [key: string]: string } = {};
  
  constructor(
    private fb: FormBuilder,
    private router: Router,
    private sanitizer: DomSanitizer,
    private uploadService: UploadService,
    private postService: PostService
  ) {
  this.postForm = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
    categories: [[''], Validators.required], // Change to array
    content: ['', [Validators.required, Validators.minLength(50)]],
    creator: [this.creatorUsername],
    visibility: ['public'],
    });
  }
  
  ngOnInit() {
    // You can add initialization logic here
  }
  
  // ========== GETTER METHODS FOR TEMPLATE ==========
  
  get uploadingFilesCount(): number {
    return Object.keys(this.uploadingFiles).length;
  }
  
  get uploadingFileIds(): string[] {
    return Object.keys(this.uploadingFiles);
  }
  
  get errorFileIds(): string[] {
    return Object.keys(this.uploadErrors);
  }
  
  get isUploading(): boolean {
    return this.uploadingFilesCount > 0;
  }
  
  get uploadedMediaCount(): number {
    return this.mediaPreviews.length;
  }
  
  // ========== CONTENT EDITOR METHODS ==========
  
  insertImage(): void {
    const imageUrl = prompt('Enter image URL (or leave empty to upload):');
    
    if (imageUrl === null) {
      return; // User cancelled
    }
    
    if (imageUrl && imageUrl.trim() !== '') {
      // Insert markdown image syntax
      this.insertAtCursor(`![Image description](${imageUrl})`);
    } else {
      // Trigger file upload
      const fileInput = document.createElement('input');
      fileInput.type = 'file';
      fileInput.accept = 'image/*';
      fileInput.multiple = true;
      
      fileInput.onchange = (event: Event) => {
        const target = event.target as HTMLInputElement;
        const files = target.files;
        if (files && files.length > 0) {
          Array.from(files).forEach(file => {
            if (this.isValidFile(file)) {
              this.uploadToCloudinary(file);
            }
          });
        }
      };
      
      fileInput.click();
    }
  }
  
  insertCode(): void {
    const language = prompt('Enter programming language (optional):', 'javascript');
    const code = prompt('Enter code snippet:');
    
    if (code !== null && code.trim() !== '') {
      const lang = language?.trim() || '';
      this.insertAtCursor(`\`\`\`${lang}\n${code}\n\`\`\``);
    }
  }
  
  insertLink(): void {
    const url = prompt('Enter URL:', 'https://');
    const text = prompt('Enter link text:', 'Link');
    
    if (url && text && url.trim() !== '' && text.trim() !== '') {
      this.insertAtCursor(`[${text}](${url})`);
    }
  }
  
formatText(command: string, event?: MouseEvent): void {
  if (event) {
    event.preventDefault();
  }
  
  const textarea = this.contentTextarea?.nativeElement;
  if (!textarea) return;
  
  const start = textarea.selectionStart;
  const end = textarea.selectionEnd;
  const selectedText = textarea.value.substring(start, end);
  
  let formattedText = '';
  
  switch (command) {
    case 'bold':
      formattedText = `**${selectedText}**`;
      break;
    case 'italic':
      formattedText = `*${selectedText}*`;
      break;
    case 'underline':
      formattedText = `<u>${selectedText}</u>`; // Keep as HTML since markdown doesn't have underline
      break;
    case 'h1':
      formattedText = `# ${selectedText}`;
      break;
    case 'h2':
      formattedText = `## ${selectedText}`;
      break;
    case 'h3':
      formattedText = `### ${selectedText}`;
      break;
    case 'ul':
      // For lists, we need to handle multiple lines
      if (selectedText.includes('\n')) {
        const lines = selectedText.split('\n');
        formattedText = lines.map(line => line.trim() ? `- ${line}` : '').join('\n');
      } else {
        formattedText = `- ${selectedText}`;
      }
      break;
    case 'ol':
      // For numbered lists
      if (selectedText.includes('\n')) {
        const lines = selectedText.split('\n');
        formattedText = lines.map((line, index) => line.trim() ? `${index + 1}. ${line}` : '').join('\n');
      } else {
        formattedText = `1. ${selectedText}`;
      }
      break;
    case 'link':
      this.insertLink();
      return;
    case 'code':
      this.insertCode();
      return;
    case 'image':
      this.insertImage();
      return;
    default:
      formattedText = selectedText;
  }
  
  this.insertAtCursor(formattedText);
}
  
  insertAtCursor(text: string): void {
    const textarea = this.contentTextarea?.nativeElement;
    if (!textarea) return;
    
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const content = this.postForm.get('content')?.value || '';
    
    const newContent = content.substring(0, start) + text + content.substring(end);
    this.postForm.get('content')?.setValue(newContent);
    
    // Restore focus and set cursor position
    setTimeout(() => {
      textarea.focus();
      const newPosition = start + text.length;
      textarea.setSelectionRange(newPosition, newPosition);
    }, 0);
  }
  
  toggleEditor(mode: 'write' | 'preview'): void {
    this.editorMode = mode;
  }
  
  // ========== MEDIA METHODS ==========
  
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const files = input.files;
    
    if (files && files.length > 0) {
      Array.from(files).forEach(file => {
        if (this.isValidFile(file)) {
          this.uploadToCloudinary(file);
        } else {
          this.showToast(`File ${file.name} is not allowed or too large.`, 'error');
        }
      });
      input.value = '';
    }
  }
  
  isValidFile(file: File): boolean {
    const maxSize = 10 * 1024 * 1024; // 10MB
    const allowedTypes = [
      'image/jpeg', 
      'image/jpg',
      'image/png', 
      'image/gif', 
      'image/webp', 
      'video/mp4'
    ];
    
    if (file.size > maxSize) {
      return false;
    }
    
    if (!allowedTypes.includes(file.type)) {
      return false;
    }
    
    return true;
  }
  
  uploadToCloudinary(file: File): void {
    const fileId = `${Date.now()}_${file.name}`;
    this.uploadingFiles[fileId] = true;
    
    // Create local preview first
    const reader = new FileReader();
    reader.onload = (e: any) => {
      const previewUrl = e.target.result;
      
      // Add to media previews with temp URL
      const mediaPreview: MediaPreview = {
        id: Date.now(),
        url: previewUrl,
        file: file,
        type: file.type.startsWith('image/') ? 'image' : 'video',
        caption: file.name.replace(/\.[^/.]+$/, ""), // Remove extension
        cloudinaryData: undefined
      };
      
      this.mediaPreviews.push(mediaPreview);
      
      // Start Cloudinary upload
      const folder = `blog/posts/${new Date().getFullYear()}/${new Date().getMonth() + 1}`;
      
      this.uploadService.uploadFile(file, folder).subscribe({
        next: (response: UploadResponse) => {
          delete this.uploadingFiles[fileId];
          delete this.uploadErrors[fileId];
          
          // Update media preview with Cloudinary data
          const index = this.mediaPreviews.findIndex(m => m.file === file);
          if (index !== -1) {
            this.mediaPreviews[index].cloudinaryData = response;
            this.mediaPreviews[index].url = response.thumbnailUrl || response.url;
          }
          
          // Insert markdown into editor
          if (file.type.startsWith('image/')) {
  this.insertAtCursor(`\n![${file.name}](${response.url})\n`);
} else if (file.type.startsWith('video/')) {
  this.insertAtCursor(`\n**Video:** ${file.name}\n\n\`\`\`\nVideo URL: ${response.url}\n\`\`\`\n`);
}
          // if (file.type.startsWith('image/')) {
          //   this.insertAtCursor(`\n![${file.name}](${response.url})\n`);
          // } else if (file.type.startsWith('video/')) {
          //   this.insertAtCursor(`\n**Video:** ${file.name}\n\n\`\`\`\nVideo URL: ${response.url}\n\`\`\`\n`);
          // }
          
          this.showToast(`${file.name} uploaded successfully!`, 'success');
        },
        error: (error) => {
          delete this.uploadingFiles[fileId];
          this.uploadErrors[fileId] = `Failed to upload: ${error.message || 'Unknown error'}`;
          
          // Remove failed upload from previews
          this.mediaPreviews = this.mediaPreviews.filter(m => m.file !== file);
          
          this.showToast(`Failed to upload ${file.name}`, 'error');
          console.error('Upload failed:', error);
        }
      });
    };
    
    reader.onerror = () => {
      delete this.uploadingFiles[fileId];
      this.showToast(`Failed to read file: ${file.name}`, 'error');
    };
    
    reader.readAsDataURL(file);
  }
  
  removeMedia(id: number): void {
    const media = this.mediaPreviews.find(m => m.id === id);
    
    if (media) {
      // If uploaded to Cloudinary, delete it
      if (media.cloudinaryData?.publicId) {
        this.uploadService.deleteFile(media.cloudinaryData.publicId).subscribe({
          next: () => {
            console.log('File deleted from Cloudinary');
          },
          error: (err) => {
            console.error('Failed to delete from Cloudinary:', err);
          }
        });
      }
      
      // Remove from local array
      this.mediaPreviews = this.mediaPreviews.filter(m => m.id !== id);
    }
  }
  
  // ========== FORM SUBMISSION ==========
  
async onSubmit(event?: Event): Promise<void> {
    if (event) event.preventDefault();
    
    // Mark all fields as touched
    Object.keys(this.postForm.controls).forEach(key => {
        const control = this.postForm.get(key);
        control?.markAsTouched();
    });
    
    if (this.postForm.invalid) {
        this.showToast('Please fill in all required fields correctly.', 'error');
        return;
    }
    
    // Add this debug log
    console.log('Form data before sending:', this.postForm.value);
    console.log('Form validity:', this.postForm.valid);
    
    this.isSubmitting = true;
    
    try {
        const postData = {
            title: this.postForm.get('title')?.value,
            categories: this.postForm.get('categories')?.value,
            content: this.postForm.get('content')?.value,
            creator: this.postForm.get('creator')?.value,
            visibility: this.postForm.get('visibility')?.value,
            dateFrom: new Date().toISOString()
        };
        
        console.log('Post data being sent:', postData);
        
        const result = await this.postService.createPost(postData);
      
      this.isSubmitting = false;
      
      if (result) {
        this.showToast('Post created successfully!', 'success');
        
        // Clear form
        this.postForm.reset({
          title: '',
          categories: [],
          content: '',
          creator: this.creatorUsername,
          visibility: 'public'
        });
        this.mediaPreviews = [];
        this.uploadErrors = {};
        
        // Navigate to home after a short delay
        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 1500);
      } else {
        this.showToast('Failed to create post. Please try again.', 'error');
      }
      
    } catch (error) {
      this.isSubmitting = false;
      console.error('Error creating post:', error);
      this.showToast('An error occurred while creating the post.', 'error');
    }
  }
  
  cancel(): void {
    if (this.postForm.dirty || this.mediaPreviews.length > 0) {
      if (confirm('You have unsaved changes. Are you sure you want to cancel?')) {
        this.router.navigate(['/home']);
      }
    } else {
      this.router.navigate(['/home']);
    }
  }
  
  // ========== HELPER METHODS ==========
  
// Simple markdown renderer for preview
renderMarkdown(text: string): SafeHtml {
  if (!text || text.trim() === '') {
    return this.sanitizer.bypassSecurityTrustHtml('');
  }
  
  let html = text;
  
  // Headers
  html = html.replace(/^### (.*$)/gim, '<h5 class="fw-bold mt-3 mb-2">$1</h5>');
  html = html.replace(/^## (.*$)/gim, '<h4 class="fw-bold mt-3 mb-2">$1</h4>');
  html = html.replace(/^# (.*$)/gim, '<h3 class="fw-bold mt-3 mb-2">$1</h3>');
  
  // Bold & Italic
  html = html.replace(/\*\*(.*?)\*\*/gim, '<strong>$1</strong>');
  html = html.replace(/\*(.*?)\*/gim, '<em>$1</em>');
  
  // Code blocks
  html = html.replace(/```([\s\S]*?)```/gim, '<pre class="bg-light p-3 rounded mb-3"><code>$1</code></pre>');
  
  // Inline code
  html = html.replace(/`(.*?)`/gim, '<code class="bg-light px-1 rounded">$1</code>');
  
  // Images
  html = html.replace(/!\[(.*?)\]\((.*?)\)/gim, 
    '<div class="text-center my-3">' +
    '<img src="$2" alt="$1" class="img-fluid rounded border" style="max-height: 300px;">' +
    '</div>');
  
  // Links
  html = html.replace(/\[(.*?)\]\((.*?)\)/gim, '<a href="$2" class="text-primary">$1</a>');
  
  // Videos (from your upload format)
  html = html.replace(/Video URL:\s*(.*?\.mp4)/gim, 
    '<div class="text-center my-3">' +
    '<video controls class="img-fluid rounded border" style="max-height: 300px;">' +
    '<source src="$1" type="video/mp4">' +
    '</video>' +
    '</div>');
  
  // Lists
  html = html.replace(/^\s*[-*+]\s+(.*)/gim, '<li>$1</li>');
  html = html.replace(/^\s*\d+\.\s+(.*)/gim, '<li>$1</li>');
  html = html.replace(/(<li>.*<\/li>\n?)+/g, '<ul class="mb-3">$&</ul>');
  
  // Blockquotes
  html = html.replace(/^>\s*(.*$)/gim, '<blockquote class="border-start ps-3 my-3">$1</blockquote>');
  
  // Horizontal rules
  html = html.replace(/^---$/gim, '<hr class="my-3">');
  
  // Convert line breaks to paragraphs
  const lines = html.split('\n');
  let result = '';
  let currentParagraph = '';
  
  for (const line of lines) {
    if (line.trim() === '') {
      if (currentParagraph) {
        result += `<p>${currentParagraph.trim()}</p>`;
        currentParagraph = '';
      }
    } else {
      // Skip if line already starts with HTML tag
      if (line.startsWith('<h') || 
          line.startsWith('<pre') || 
          line.startsWith('<img') || 
          line.startsWith('<video') || 
          line.startsWith('<ul') || 
          line.startsWith('<blockquote') || 
          line.startsWith('<hr') || 
          line.startsWith('<div')) {
        if (currentParagraph) {
          result += `<p>${currentParagraph.trim()}</p>`;
          currentParagraph = '';
        }
        result += line + '\n';
      } else {
        currentParagraph += line + ' ';
      }
    }
  }
  
  if (currentParagraph) {
    result += `<p>${currentParagraph.trim()}</p>`;
  }
  console.log('Rendered HTML:', result);
  return this.sanitizer.bypassSecurityTrustHtml(result);
}
  
  showToast(message: string, type: 'success' | 'error' | 'info'): void {
    // Remove any existing toasts
    const existingToasts = document.querySelectorAll('.custom-toast');
    existingToasts.forEach(toast => toast.remove());
    
    const toast = document.createElement('div');
    toast.className = `custom-toast position-fixed top-0 end-0 m-3 p-3 rounded-3 shadow-sm`;
    
    let bgColor = '';
    let icon = '';
    
    switch (type) {
      case 'success':
        bgColor = 'bg-success text-white';
        icon = 'bi-check-circle-fill';
        break;
      case 'error':
        bgColor = 'bg-danger text-white';
        icon = 'bi-exclamation-circle-fill';
        break;
      case 'info':
        bgColor = 'bg-info text-white';
        icon = 'bi-info-circle-fill';
        break;
    }
    
    toast.className += ` ${bgColor}`;
    toast.style.zIndex = '9999';
    toast.innerHTML = `
      <div class="d-flex align-items-center">
        <i class="bi ${icon} me-2"></i>
        <span>${message}</span>
      </div>
    `;
    
    document.body.appendChild(toast);
    
    // Auto-remove after 3 seconds
    setTimeout(() => {
      if (document.body.contains(toast)) {
        document.body.removeChild(toast);
      }
    }, 3000);
  }

  @HostListener('document:keydown', ['$event'])
  handleKeyboardShortcuts(event: KeyboardEvent): void {
    const isCtrl = event.ctrlKey || event.metaKey;
    
    if (!isCtrl) return;
    
    // Only prevent default for specific shortcuts
    switch (event.key.toLowerCase()) {
      case 's':
        event.preventDefault();
        this.onSubmit();
        break;
      case 'b':
        if (this.editorMode === 'write') {
          event.preventDefault();
          this.formatText('bold');
        }
        break;
      case 'i':
        if (this.editorMode === 'write') {
          event.preventDefault();
          this.formatText('italic');
        }
        break;
      case 'u':
        if (this.editorMode === 'write') {
          event.preventDefault();
          this.formatText('underline');
        }
        break;
      case 'p':
        if (this.editorMode === 'write') {
          event.preventDefault();
          this.toggleEditor('preview');
        }
        break;
      case 'w':
        if (this.editorMode === 'preview') {
          event.preventDefault();
          this.toggleEditor('write');
        }
        break;
    }
  }
}