import { Component } from '@angular/core';
import { User } from '../../models/user';
import { ProfileService } from '../../services/profile';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class ProfileComponent {
  isEditing: boolean = false;
  isLoading: boolean = true;
  user: User | null = null;
  followers: boolean = false;
  following: boolean = false;
  constructor(private profileservice:ProfileService){}

  ngOnInit(): void {
    this.profileservice.fetchUserProfile().subscribe(user => {
      console.log('User profile fetched:', user);
      this.user = user;
    });
    this.isLoading = false;
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
  }
  toggleFollowers(): void {
    this.followers = !this.followers;
  }
  toggleFollowing(): void {
    this.following = !this.following;
  }

  onSubmit(): void {
    this.profileservice.updateUserProfile(this.user!);
    console.log('Updated user profile:', this.user);
    this.isEditing = false;
  }
}
