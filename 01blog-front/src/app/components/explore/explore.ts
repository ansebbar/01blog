// import { Component } from '@angular/core';

// @Component({
//   selector: 'app-explore',
//   imports: [],
//   templateUrl: './explore.html',
//   styleUrl: './explore.css',
// })
// export class Explore {
//   searchQuery: string = '';
//   usersprev: any[] = [];
//   allusers: any[] = [];






//     onSearch(): void {
//       const query = this.searchQuery.toLowerCase();
//       if (this.searchQuery.trim() === '') {
//         if(this.usersprev.length > 0) {
//           this.allusers = this.usersprev;
//           return;
//         }
//       } else {
//           if(this.usersprev.length === 0) {
//         this.usersprev = this.allusers;
//         this.allusers = this.allusers.filter(user =>
//           user.username.toLowerCase().includes(query) ||
//           user.firstName.toLowerCase().includes(query) ||
//           user.lastName.toLowerCase().includes(query)
//         );
//       }else {
//         this.allusers = this.usersprev.filter(user =>
//           user.username.toLowerCase().includes(query) ||
//           user.firstName.toLowerCase().includes(query) ||
//           user.lastName.toLowerCase().includes(query)
//         );
//       }
//     }
//     }
// }


import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

interface User {
  id: string;
  username: string;
  avatarUrl?: string;
  rateadmin: string;
  followersCount?: number;
  followingCount?: number;
  isFollowing?: boolean;
}

@Component({
  selector: 'app-explore',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './explore.component.html',
  styleUrls: ['./explore.component.css']
})
export class ExploreComponent implements OnInit {
  allUsers: User[] = [];
  filteredUsers: User[] = [];
  currentUserId: string = '';
  currentUsername: string = '';
  
  isLoading: boolean = true;
  searchQuery: string = '';
  
  followingMap: Map<string, boolean> = new Map();
  isFollowLoading: { [key: string]: boolean } = {};
  
  private apiUrl = 'http://localhost:3000/api';

  constructor(private http: HttpClient) {}

  async ngOnInit() {
    await this.loadCurrentUser();
    await this.loadUsers();
  }

  async loadCurrentUser() {
    try {
      const userData = localStorage.getItem('username');
      if (userData) {
        const user = JSON.parse(userData);
        this.currentUserId = user.id;
        this.currentUsername = user.username;
      } else {
        await this.fetchCurrentUserFromToken();
      }
    } catch (error) {
      console.error('Error loading current user:', error);
    }
  }

  async fetchCurrentUserFromToken() {
    try {
      const token = localStorage.getItem('token');
      if (token) {
        const user = await firstValueFrom(
          this.http.get<any>(`${this.apiUrl}/auth/me`, {
            headers: { Authorization: `Bearer ${token}` }
          })
        );
        this.currentUserId = user.id;
        this.currentUsername = user.username;
      }
    } catch (error) {
      console.error('Error fetching current user:', error);
    }
  }

  async loadUsers() {
    try {
      this.isLoading = true;
      
      const users = await firstValueFrom(
        this.http.get<User[]>(`${this.apiUrl}/users`)
      );
      
      this.allUsers = users.filter(user => user.id !== this.currentUserId);
      
      // Load following status for each user
      await this.loadFollowingStatus();
      
      this.filteredUsers = [...this.allUsers];
      this.isLoading = false;
    } catch (error) {
      console.error('Error loading users:', error);
      this.isLoading = false;
    }
  }

  async loadFollowingStatus() {
    try {
      const token = localStorage.getItem('token');
      if (!token) return;

      // Fetch the list of users the current user is following
      const following = await firstValueFrom(
        this.http.get<any[]>(`${this.apiUrl}/users/${this.currentUserId}/following`, {
          headers: { Authorization: `Bearer ${token}` }
        })
      );

      // Create a map of following status
      this.followingMap.clear();
      following.forEach(f => {
        this.followingMap.set(f.id, true);
      });

      // Update each user's isFollowing property
      this.allUsers.forEach(user => {
        user.isFollowing = this.followingMap.has(user.id);
      });

    } catch (error) {
      console.error('Error loading following status:', error);
    }
  }

  isFollowing(userId: string): boolean {
    return this.followingMap.has(userId);
  }

  async toggleFollow(user: User) {
    // Don't allow following yourself
    if (user.id === this.currentUserId) return;

    // Set loading state
    this.isFollowLoading[user.id] = true;

    try {
      const token = localStorage.getItem('token');
      if (!token) {
        // Redirect to login or show message
        console.error('No token found');
        return;
      }

      const isCurrentlyFollowing = this.isFollowing(user.id);

      if (isCurrentlyFollowing) {
        // Unfollow
        await firstValueFrom(
          this.http.delete(`${this.apiUrl}/users/${this.currentUserId}/follow/${user.id}`, {
            headers: { Authorization: `Bearer ${token}` }
          })
        );
        
        // Update local state
        this.followingMap.delete(user.id);
        user.isFollowing = false;
        if (user.followersCount !== undefined) {
          user.followersCount--;
        }

      } else {
        // Follow
        await firstValueFrom(
          this.http.post(`${this.apiUrl}/users/${this.currentUserId}/follow/${user.id}`, {}, {
            headers: { Authorization: `Bearer ${token}` }
          })
        );
        
        // Update local state
        this.followingMap.set(user.id, true);
        user.isFollowing = true;
        if (user.followersCount !== undefined) {
          user.followersCount++;
        }
      }

      // Optional: Show success message
      console.log(`${isCurrentlyFollowing ? 'Unfollowed' : 'Followed'} ${user.username}`);

    } catch (error) {
      console.error('Error toggling follow:', error);
      // Optional: Show error message to user
      alert(`Failed to ${this.isFollowing(user.id) ? 'unfollow' : 'follow'} ${user.username}. Please try again.`);
    } finally {
      // Clear loading state
      delete this.isFollowLoading[user.id];
    }
  }

  onSearch() {
    if (!this.searchQuery.trim()) {
      this.filteredUsers = [...this.allUsers];
      return;
    }

    const query = this.searchQuery.toLowerCase().trim();
    this.filteredUsers = this.allUsers.filter(user => 
      user.username.toLowerCase().includes(query)
    );
  }

  clearSearch() {
    this.searchQuery = '';
    this.filteredUsers = [...this.allUsers];
  }
}