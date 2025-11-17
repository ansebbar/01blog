import { computed, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest } from '../models/login-request';
import { RegisterRequest } from '../models/register-request';
import { AuthResponse } from '../models/auth-response';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;

    // private auth = signal(false);
  //   private isAuthenticated = signal<boolean>(this.checkInitialAuthState());
  // private currentUsername = signal<string | null>(this.getUsername());

  // // Public computed signals for components to use
  // readonly authState = computed(() => ({
  //   isLoggedIn: this.isAuthenticated(),
  //   username: this.currentUsername()
  // }));
  
  constructor(private http: HttpClient) { }
  
  register(registerRequest: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, registerRequest)
      .pipe(
        tap(response => {
          this.saveToken(response.token);
          this.saveUserInfo(response);
          // this.setAuthState(true, response.username);
        })
      );
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

  // private setAuthState(isLoggedIn: boolean, username: string | null = null): void {
  //   this.isAuthenticated.set(isLoggedIn);
  //   this.currentUsername.set(username);
  // }

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
  }
  
  getToken(): string | null {
    return localStorage.getItem('authToken');
  }
  
  getUsername(): string | null {
    return localStorage.getItem('username');
  }
  
  isLoggedIn(): boolean {
    return !!this.getToken();
  }
  
  logout(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('username');
    localStorage.removeItem('email');
    // this.auth.set(false);
  }
}