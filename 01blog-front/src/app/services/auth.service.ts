import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest } from '../models/login-request';
import { RegisterRequest } from '../models/register-request';
import { AuthResponse } from '../models/auth-response';
import { User } from '../models/user';
import { FpasswordRequest } from '../models/fpassword-request';
import { Router } from '@angular/router';
import { NavbarComponent } from '../components/navbar/navbar';
import { Navservice } from './navservice';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private pin: string = "";

    // private auth = signal(false);
  //   private isAuthenticated = signal<boolean>(this.checkInitialAuthState());
  // private currentUsername = signal<string | null>(this.getUsername());

  // // Public computed signals for components to use
  // readonly authState = computed(() => ({
  //   isLoggedIn: this.isAuthenticated(),
  //   username: this.currentUsername()
  // }));

  constructor(private http: HttpClient, private router: Router) { }

  private nav = inject(Navservice);

  
  
  register(registerRequest: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, registerRequest);
  }
  
  login(loginRequest: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, loginRequest)
      .pipe(
        tap(response => {
          this.saveToken(response.token);
          this.saveUserInfo(response);
          // this.setAuthState(true, response.username);
        })
      );
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  fpassword(email: string): Observable<boolean> {
      return this.http.post<boolean>(`${this.apiUrl}/auth/fpassword?email=${email}`,{});
  }
  // private setAuthState(isLoggedIn: boolean, username: string | null = null): void {
  //   this.isAuthenticated.set(isLoggedIn);
  //   this.currentUsername.set(username);
  // }

  pinVerify(pin: string, email: string): Observable<boolean> {
    return this.http.post<boolean>(`${this.apiUrl}/auth/verify-pin?pin=${pin}&email=${email}`, {});
  }

  getUserInfo(username:String | null): Observable<User> {
    if (!username) {
      throw new Error('Username is required to fetch user info');
    }
    console.log('Fetching user info for username:', username);
    return this.http.get<User>(`${this.apiUrl}/profile?username=${username}`,);
  }
  
  private saveToken(token: string): void {
    localStorage.setItem('authToken', token);
  }

  checkInitialAuthState(): boolean {
    return !!this.getToken();
  }
  
  private saveUserInfo(authResponse: AuthResponse): void {
    localStorage.setItem('username', authResponse.username);
    localStorage.setItem('email', authResponse.email);
    localStorage.setItem('userid', authResponse.id);
    console.log('Saving profile image URL:', authResponse.profileImageUrl);
    localStorage.setItem('userProfile', authResponse.profileImageUrl || '');
  }

   saveavatar(username: string, avatarUrl: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/profile/avatar?username=${username}&avatarUrl=${avatarUrl}`, {});
  }
  
  getToken(): string | null {
    return localStorage.getItem('authToken');
  }
  
  getUsername(): string | null {
    return localStorage.getItem('username');
  }
  getProfileImageUrl(): string | null {
    return localStorage.getItem('userProfile');
  }
  
  isLoggedIn(): Observable<boolean> {
    // console.log("Checking login status in AuthService...");
    // console.log("helllo");
    // if (this.getToken() != null && this.getUsername() != null) {
    
      console.log("User is logged in. Validating token...", this.getUsername(), this.getToken());
      return this.http.get<boolean>(`${this.apiUrl}/auth/validate-token?username=${localStorage.getItem('username')}&token=${localStorage.getItem('authToken')}`,);
    // }
    // console.log("User is not logged in.");
    // return false;
    // return !!this.getToken();
  }
  
  logout(): void {
    // console.log("Logging out user.");

    // this.navbarComponent.updateLogin(false);
    // this.nav.updateLogin(false);
    this.nav.setLoginStatus(false);
    localStorage.removeItem('username');
    localStorage.removeItem('email');
    this.http.put<void>(`${this.apiUrl}/auth/logout`, {}, {
      headers: {
        'Authorization': `${this.getToken()}`
      }
    }).subscribe({
      next: () => {
        console.log("Logout successful on server.");
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error("Error during logout:", error);
        this.router.navigate(['/login']);
      }
    });
    localStorage.removeItem('authToken');

  }
}