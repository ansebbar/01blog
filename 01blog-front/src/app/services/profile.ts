import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { User } from '../models/user';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
    private apiUrl = environment.apiUrl;
  
  user: User | null = null;
  constructor(private authService: AuthService ,private http:HttpClient) {}


getCurrentUsername(): string | null {
  const userStr = localStorage.getItem('currentUser');
  if (userStr) {
    const user = JSON.parse(userStr);
    return user.username;
  }
  return null;
}

toggleFollow(followingId: number): Observable<any> {
  console.log("toggleFollow called with followingId:", followingId);
  return this.http.post<any>(`${this.apiUrl}/follow/toggle`, { userIdToFollow:followingId });
}

fetchUserProfile(): Observable<User> {
  return this.http.get<User>(`${this.apiUrl}/myprofile`);
}
getallusers(userid: string): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/allusers?userid=${userid}`);
}
  fetchUserProfileByUsername(username: string, currentUsername: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/profile?username=${username}&currentuser=${currentUsername}`);
  }

updateUserProfile(updatedUser: User): Observable<User> {
    
  try {
    return this.http.put<User>(`${this.apiUrl}/updateuserinfo`, updatedUser);
  } catch (error) {
    console.error('Failed to update user profile:', error);
    throw error;
  }
  }
  changePassword(fpasswordRequest:any):void{
    this.http.post(`${this.apiUrl}/auth/fpassword`,fpasswordRequest).subscribe({
      next: () => {
        alert('Password changed successfully:');
      },
      error: (error) => {
        console.error('Failed to change password:', error);
      },
    });
  }

  dashboardchange(id: number,type:string): Observable<any> {
    return this.http.post(`${this.apiUrl}/dashboardchange`, { id, type , jwt: this.authService.getToken() });
  }
  reportUser(username: string, reason: string): void{
    if (!username) {
      console.error('No user to report');
      return;
    }
    console.log("Reporting user:", username, "for reason:", reason);
    this.http.post(`${this.apiUrl}/profile/report`, {username:localStorage.getItem('username'),reportedusername:username,raison:reason}).subscribe({
      next: () => {
        alert('User reported. Thank you for your feedback.');
      },
      error: (error) => {
        console.error('Error reporting user:', error);
        alert('Failed to report user. Please try again later.');
      }
    });
  }
}
