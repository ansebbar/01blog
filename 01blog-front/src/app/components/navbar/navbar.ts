import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
// import { LoginComponent } from '../login/login';


//after login, show username and logout button in navbar
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class NavbarComponent implements OnInit {
  isLoggedInn: boolean = false;
  username: string | null = '';
  log = inject(AuthService);
  
  constructor(private authService: AuthService) {}
  
  ngOnInit(): void {
    this.isLoggedInn = this.authService.isLoggedIn();
    this.username = this.authService.getUsername();
  }

  // updateLogin(login : boolean): void {
  //   this.login.set(login);
  //   this.isLoggedIn = this.authService.isLoggedIn();
  //   this.username = this.authService.getUsername();
  // }
  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
  
  logout(): void {
    this.authService.logout();
    window.location.reload();
  }

}