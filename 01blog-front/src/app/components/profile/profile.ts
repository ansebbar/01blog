

import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../../models/user';
import { ProfileService } from '../../services/profile';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class ProfileComponent implements OnInit, OnDestroy {
  isEditing: boolean = false;
  isLoading: boolean = true;
  user: any | null = null;
  followers: boolean = false;
  following: boolean = false;
  isFollowing: boolean = false;
  reported: boolean = false;
  reportReason: string = '';
  myprofile: boolean = false;
  passwordModalOpen: boolean = false;
  usernameParam: string | null = null;
  confirmpass: string = "";
  private routeSub: Subscription | undefined;
  erroorMessage: string = '';
  

  constructor(
    private profileservice: ProfileService,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  activeSection: 'posts' | 'followers' | 'following' = 'posts';
showPostsSection: boolean = true;
showFollowersSection: boolean = false;
showFollowingSection: boolean = false;

onPostsClick(): void {
  this.activeSection = 'posts';
  this.showPostsSection = true;
  this.showFollowersSection = false;
  this.showFollowingSection = false;
}

onFollowersClick(): void {
  this.activeSection = 'followers';
  this.showPostsSection = false;
  this.showFollowersSection = true;
  this.showFollowingSection = false;
}

onFollowingClick(): void {
  this.activeSection = 'following';
  this.showPostsSection = false;
  this.showFollowersSection = false;
  this.showFollowingSection = true;
}

// Update the existing toggle methods to use new properties
// toggleFollowers(): void {
//   this.followers = !this.followers;
//   if (this.followers) {
//     this.onFollowersClick();
//   } else {
//     this.onPostsClick();
//   }
// }

// toggleFollowing(): void {
//   this.following = !this.following;
//   if (this.following) {
//     this.onFollowingClick();
//   } else {
//     this.onPostsClick();
//   }
// }

reportUser() {
  if (!this.user) return;
  alert('User reported. Thank you for your feedback.');
  this.profileservice.reportUser(this.user.username, this.reportReason);
  this.reported = false;//await response from backend before setting to false
  
}

      onAvatarChange(event: any) {
    const file = event.target.files[0];
    if (file) {
      // Validate file
      if (!file.type.startsWith('image/')) {
        alert('Please select an image file');
        return;
      }
      
      if (file.size > 5 * 1024 * 1024) { // 5MB limit
        alert('Image size should be less than 5MB');
        return;
      }
      
      // Create preview
      const reader = new FileReader();
      reader.onload = (e: any) => {
        if (this.user)
          this.user.avatarUrl = e.target.result;
      };
      reader.readAsDataURL(file);
      // You would typically upload to server here
      // this.uploadAvatar(file).subscribe(...)
    }
  }

  //   toggleEdit(): void {
  //   this.isEditing = !this.isEditing;
  // }
  toggleFollowers(): void {
    this.followers = !this.followers;
  }
  toggleFollowing(): void {
    this.following = !this.following;
  }

  ngOnInit(): void {
    this.isLoading = true;
    this.routeSub = this.route.params.subscribe(params => {
      this.usernameParam = params['username'];
      console.log("usernameParam:", this.usernameParam);
      console.log("currentUsername:", localStorage.getItem('username'));
      if (this.usernameParam == localStorage.getItem('username')) {
        console.log("my profile");
        this.usernameParam = null;
      } else {
        this.myprofile = false;
      }
      this.loadProfile();
    });
  }

  toggleFollow(): void {
  if (!this.user || this.myprofile) return;
  
  // Call your follow/unfollow service
  this.profileservice.toggleFollow(this.user.id).subscribe({
    next: (response) => {
      // Update follow status
      this.isFollowing = !this.isFollowing;
      if (this.user) {
        if (this.isFollowing) {
          this.user.followersCount = (this.user.followersCount || 0) + 1;
        } else {
          this.user.followersCount = Math.max(0, (this.user.followersCount || 1) - 1);
        }
      }
    },
    error: (error) => {
      console.error('Error toggling follow:', error);
    }
  });
}

  loadProfile(): void {
    
    if (this.usernameParam) {
      this.profileservice.fetchUserProfileByUsername(this.usernameParam, localStorage.getItem('username') || '').subscribe({
        next: (user) => {
          this.user = user;
          console.log('Profile loaded:', this.user);
          if (this.user.followersCount > 0 && this.user.followers?.some((f: User) => f?.username === localStorage.getItem('username'))) {
            this.isFollowing = true;
          } else {
            this.isFollowing = false;
          }
          this.myprofile = false;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading profile:', error);
          this.user = null;
          this.isLoading = false;
        }
      });
    } else {
      this.profileservice.fetchUserProfile().subscribe({
        next: (user) => {
          this.user = user;
            console.log('Profile loaded:', this.user);

          this.myprofile = true;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading profile:', error);
          this.user = null;
          this.isLoading = false;
        }
      });
    }
  }

  checkIfMyProfile(): void {
    const currentUsername = this.profileservice.getCurrentUsername();
    if (currentUsername && this.user && this.user.username === currentUsername) {
      this.myprofile = true;
    } else {
      this.myprofile = false;
    }
  }

  toggleEdit(): void {
    if (this.myprofile) {
      this.isEditing = !this.isEditing;
    }
  }


  checkdata(data: User): boolean {
    if (!data.firstName || !data.lastName ) {
      this.erroorMessage = 'Please fill in all required fields.';
      return true;
    }
    if (data.firstName.length < 2) {
      this.erroorMessage = 'First name must be at least 2 characters.';
      return true;
    }
    if (data.lastName.length < 2) {
      this.erroorMessage = 'Last name must be at least 2 characters.';
      return true;
    }
    if(this.passwordModalOpen && this.user?.password!==this.confirmpass){
      this.erroorMessage = 'Passwords do not match';
      return true;
    }else if (this.passwordModalOpen && this.user?.password && this.user?.password.length < 8){
      this.erroorMessage = 'Password must be at least 8 characters long';
      return true ;
    }
    // if (!this.authService.isValidEmail(data.email)) {
    //   this.erroorMessage = 'Please enter a valid email address.';
    //   return true;
    // }
    return false;
  }

  onSubmit(): void {
    this.erroorMessage = '';
    if (!this.checkdata(this.user!)) {
    if (this.user && this.myprofile) {
      // console.log('Submitting profile update:', this.user);
      try {
      this.profileservice.updateUserProfile(this.user).subscribe({
        next: (updatedUser) => {
          // console.log('Updated user profile:', updatedUser,this.user);
          this.user = updatedUser;
          // console.log('Updated user profile:', updatedUser,this.user);
          localStorage.setItem('userProfile', this.user.avatarUrl || '');

          this.isEditing = false;
          console.log('Profile updated successfully');
        }
      });
      } catch (error) {
        this.erroorMessage = 'Failed to update profile. Please try again.';
        console.error('Failed to update user profile:', error);
    }
  }
  }
}
  
  removeAvatar() {
    if(this.user)
      this.user.avatarUrl = "https://ui-avatars.com/api/?name="+this.user.firstName+"+"+this.user.lastName+"&background=4f46e5&color=fff";
    // Call API to remove avatar
  }
  
  openPasswordModal() {
    // Implement password change modal
    // console.log('Open password change modal');
    if (this.passwordModalOpen) {
      this.passwordModalOpen = false;
    } else {
      this.passwordModalOpen = true;
    }
  }
  
  reloadProfile() {
    this.isLoading = true;
    // Fetch profile data again
    // this.userService.getProfile().subscribe(...)
  }
  
  // Utility function to format numbers
  formatNumber(num: number): string {
    if (num >= 1000) {
      return (num / 1000).toFixed(1) + 'k';
    }
    return num.toString();
  }

   ngOnDestroy(): void {
    if (this.routeSub) {
      this.routeSub.unsubscribe();
    }
  }
}
