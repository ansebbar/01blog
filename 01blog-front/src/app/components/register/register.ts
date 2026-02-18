import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../models/register-request';
import { UploadService } from '../../services/upload-service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {
  registerRequest: RegisterRequest = {
    email: '',
    username: '',
    password: '',
    firstName: '',
    lastName: '',
    display_name: '',
    avatarUrl: '',
    bio: '',
    dateOfBirth: ''
  };

  avatarFile: File | null = null;
  avatarPreview: string = '';
  isUploadingAvatar: boolean = false;
  uploadedAvatarUrl: string = '';
  uploadFailed: boolean = false;
  errorMessage: string = '';
  isLoading: boolean = false;
  
  constructor(private authService: AuthService,
     private router: Router,
    private uploadService: UploadService ) {}

    ngonInit(): void {
      if (this.authService.isLoggedIn()) {
        this.router.navigate(['/home']);
      }
    }
  
  ngAfterViewInit() {
  // const toggleBtn = document.getElementById('togglePassword');
  // const passwordInput = document.getElementById('password') as HTMLInputElement;
  
  // if (toggleBtn && passwordInput) {
  //   toggleBtn.addEventListener('click', () => {
  //     const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
  //     passwordInput.setAttribute('type', type);
      
  //     const icon = toggleBtn.querySelector('i');
  //     if (icon) {
  //       icon.classList.toggle('bi-eye');
  //       icon.classList.toggle('bi-eye-slash');
  //     }
  //   });
  // }
  
  const avatarInput = document.getElementById('avatarUpload');
  const avatarPlaceholder = document.querySelector('.avatar-placeholder');
  
  if (avatarInput && avatarPlaceholder) {
    avatarInput.addEventListener('change', (event: any) => {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e: any) => {
          avatarPlaceholder.innerHTML = `<img src="${e.target.result}" class="rounded-circle w-100 h-100" style="object-fit: cover;">`;
        };
        reader.readAsDataURL(file);
      }
    });
  }
}

//from heeeerre

formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// Update the onFileSelected method
onFileSelected(event: any): void {
  const file: File = event.target.files[0];
  if (file) {
    if (!this.isValidAvatar(file)) {
      return;
    }
    
    this.avatarFile = file;
    this.uploadedAvatarUrl = '';
    this.uploadFailed = false;
    
    const reader = new FileReader();
    reader.onload = () => {
      this.avatarPreview = reader.result as string;
      this.updateAvatarPreview(this.avatarPreview);
    };
    reader.readAsDataURL(file);
    
  }
}

uploadAvatarToCloudinary(file: File, username: string): void {
  this.isUploadingAvatar = true;
  this.uploadFailed = false;
  // const username: string = localStorage.getItem('username') || '';
  
  this.uploadService.uploadAvatar(file, username).subscribe({
    next: (response) => {
      this.isUploadingAvatar = false;
      this.uploadedAvatarUrl = response.url;
      localStorage.setItem('userProfile', response.url);

      this.registerRequest.avatarUrl = response.url;
      this.updateAvatarPreview(response.url);
      
      console.log('Avatar uploaded to Cloudinary:', response.url);
      this.authService.saveavatar(username, response.url).subscribe();
    },
    error: (error) => {
      this.isUploadingAvatar = false;
      this.uploadFailed = true;
      console.error('Avatar upload failed:', error);
      
      this.showUploadError();
    }
  });
}

showUploadError(): void {
  const errorDiv = document.createElement('div');
  errorDiv.className = 'position-fixed top-0 end-0 m-3 p-3 bg-danger text-white rounded-3 shadow';
  errorDiv.innerHTML = `
    <div class="d-flex align-items-center">
      <i class="bi bi-exclamation-triangle-fill me-2"></i>
      <span>Avatar upload failed. You can continue with local image or try again.</span>
    </div>
  `;
  document.body.appendChild(errorDiv);
  
  setTimeout(() => {
    if (document.body.contains(errorDiv)) {
      document.body.removeChild(errorDiv);
    }
  }, 5000);
}

  isValidAvatar(file: File): boolean {
    const maxSize = 5 * 1024 * 1024;
    const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
    
    if (file.size > maxSize) {
      alert('Avatar image must be less than 5MB');
      return false;
    }
    
    if (!allowedTypes.includes(file.type)) {
      alert('Only JPG, PNG, GIF, or WebP images are allowed');
      return false;
    }
    
    return true;
  }


  updateAvatarPreview(url: string): void {
    const avatarPlaceholder = document.querySelector('.avatar-placeholder');
    if (avatarPlaceholder) {
      avatarPlaceholder.innerHTML = `
        <img src="${url}" 
             class="rounded-circle w-100 h-100" 
             style="object-fit: cover; border: 3px solid #4CAF50;">
      `;
    }
  }

  checknaming(name: string, type: string): boolean {
    if (name.length < 3) {
      this.errorMessage = `${type} must be at least 3 characters.`;
      return true;
    }
    if (name.length > 20) {
      this.errorMessage = `${type} must be less than 20 characters.`;
      return true;
    }
    if (name.split(' ').length > 2) {
      this.errorMessage = `${type} must contain at most 2 words.`;
      return true;
    }
    if (!/^[a-zA-Z\s]+$/.test(name)) {
      this.errorMessage = `${type} must contain only letters and spaces.`;
      return true;
    }
    return false;
  }

  checkdata(data: RegisterRequest): boolean {
    if (!data.firstName || !data.lastName || !data.email || !data.username || !data.password) {
      this.errorMessage = 'Please fill in all required fields.';
      return true;
    }
    if (this.checknaming(data.firstName, 'First name') || this.checknaming(data.lastName, 'Last name')) {
      return true;
    }
    if (data.username.length < 4) {
      this.errorMessage = 'Username must be at least 4 characters.';
      return true;
    }else if (!/^[a-zA-Z]{3}/.test(data.username.substring(0, 3))) { //the 3 first characters must be letters
      this.errorMessage = 'Username must start with at least 3 letters.';
      return true;
    }else if (data.username.length > 20) {
      this.errorMessage = 'Username must be less than 20 characters.';
      return true;
    }else if (!/^[a-zA-Z0-9_]+$/.test(data.username)) {
      this.errorMessage = 'Username can only contain letters, numbers, and underscores.';
      return true;
    }
    if (data.password.length < 8 || data.password.length > 20) {
      this.errorMessage = 'Password must be at least 8 characters and less than 20 characters.';
      return true;
    }
    if (!this.authService.isValidEmail(data.email)) {
      this.errorMessage = 'Please enter a valid email address.';
      return true;
    }
    return false;
  }



  onSubmit(): void {
    
    this.isLoading = true;
    this.errorMessage = '';

    if (this.checkdata(this.registerRequest)) {
      this.isLoading = false;
      return;
    }
    this.authService.register(this.registerRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.router.navigate(['/login']);
        if (this.avatarFile && !this.uploadedAvatarUrl) {
              this.uploadAvatarToCloudinary(this.avatarFile, this.registerRequest.username);
        }else{
          this.authService.saveavatar(this.registerRequest.username, "https://ui-avatars.com/api/?name="+this.registerRequest.firstName+"+"+this.registerRequest.lastName+"&background=4f46e5&color=fff").subscribe();
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Registration failed. Please try again.';
        console.error('Registration error:', error);
      }
    });
    

  }

  // Optional: Add a retry upload button in template
  retryAvatarUpload(): void {
    if (this.avatarFile) {
      this.uploadAvatarToCloudinary(this.avatarFile, this.registerRequest.username);
    }
  }
}