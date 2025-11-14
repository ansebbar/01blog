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


  ///hna rakkkkkkkk
  fetchUserProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/profile?username=${this.authService.getUsername()}`);

}

updateUserProfile(updatedUser: User): void {
    this.http.put<User>(`${this.apiUrl}/api/updateuserinfo`, updatedUser).subscribe({
      next: (response) => {
        // this.user = response;
        console.log('User profile updated successfully:', response);
      },
      error: (error) => {
        console.error('Failed to update user profile:', error);
      },
    });
  }


}
