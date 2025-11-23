// post-create.component.ts
import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PostService } from '../../services/post-service';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-post-create',
  templateUrl: './post-creation.html',
  styleUrls: ['./post-creation.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class PostCreateComponent {
  postForm: FormGroup;
  selectedFiles: File[] = [];
  mediaPreviews: { url: string, type: string, file: File }[] = [];
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private postService: PostService
  ) {
    this.postForm = this.createForm();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5)]],
      content: ['', [Validators.required, Validators.minLength(50)]],
      // excerpt: ['', [Validators.maxLength(200)]],
      // tags: [''],
      category: ['', Validators.required]
    });
  }
   @ViewChild('fileInput') fileInput!: ElementRef;

  triggerFileInput(type: string): void {
    const input = this.fileInput.nativeElement as HTMLInputElement;
    if (type === 'image') {
      input.accept = 'image/*';
    } else if (type === 'video') {
      input.accept = 'video/*';
    }
    input.click();
  }

  onFileSelected(event: any): void {
    const files: FileList = event.target.files;
    
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      if (file.size > 10 * 1024 * 1024) {
        alert(`File ${file.name} is too large. Maximum size is 10MB.`);
        continue;
      }

      // Validate file type
      if (!file.type.startsWith('image/') && !file.type.startsWith('video/')) {
        alert(`File ${file.name} is not a supported image or video format.`);
        continue;
      }

      this.selectedFiles.push(file);
      this.createMediaPreview(file);
    }

    // Reset file input
    event.target.value = '';
  }

  createMediaPreview(file: File): void {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      const mediaType = file.type.startsWith('image/') ? 'image' : 'video';
      this.mediaPreviews.push({
        url: e.target.result,
        type: mediaType,
        file: file
      });
    };
    reader.readAsDataURL(file);
  }

  removeMedia(index: number): void {
    this.mediaPreviews.splice(index, 1);
    this.selectedFiles.splice(index, 1);
  }

  getVideoType(file: File): string {
    return file.type || 'video/mp4';
  }

  formatText(command: string): void {
    const textarea = document.getElementById('content') as HTMLTextAreaElement;
    textarea.focus();
    document.execCommand(command, false, '');
  }

  onSubmit(): void {
    if (this.postForm.valid) {
      this.isSubmitting = true;
      
      const formData = new FormData();
      
      formData.append('title', this.postForm.get('title')?.value);
      formData.append('content', this.postForm.get('content')?.value);
      // formData.append('excerpt', this.postForm.get('excerpt')?.value);
      formData.append('category', this.postForm.get('category')?.value);
      // formData.append('tags', this.postForm.get('tags')?.value);

      // Add media files
      this.selectedFiles.forEach(file => {
        formData.append('media', file);
      });

      this.postService.createPost(formData).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          alert('Post created successfully!');
          this.resetForm();
        },
        error: (error) => {
          this.isSubmitting = false;
          alert('Error creating post: ' + error.message);
        }
      });
    }
  }

  saveDraft(): void {
    // Similar to onSubmit but with draft status
    console.log('Saving as draft...');
  }

  cancel(): void {
    if (confirm('Are you sure you want to cancel? Unsaved changes will be lost.')) {
      this.resetForm();
      // Navigate back
    }
  }

  resetForm(): void {
    this.postForm.reset();
    this.selectedFiles = [];
    this.mediaPreviews = [];
  }
}