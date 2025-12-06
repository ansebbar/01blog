import { Component, inject, OnInit, signal, ViewChild } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
// import { Sidebar } from '../sidebar/sidebar';
import { SidebarService} from '../../services/sidebar.service';
// import { LoginComponent } from '../login/login';


@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class NavbarComponent implements OnInit {
  isLoggedInn: boolean = false;
  username: string | null = null;
  profileImageUrl: string | null = null; // Default profile image path
  // log = inject(AuthService);
  //  @ViewChild(Sidebar) sidebarComponent!: Sidebar;
  private authService = inject(AuthService);
  private sidebarService = inject(SidebarService);  
  ngOnInit(): void {
    // this.isLoggedInn = this.authService.isLoggedIn();
    this.isLoggedIn();
    if (this.isLoggedInn) {
      this.profileImageUrl = this.authService.getProfileImageUrl();
      if (!this.profileImageUrl)
        this.profileImageUrl =  "https://i.pinimg.com/474x/18/b9/ff/18b9ffb2a8a791d50213a9d595c4dd52.jpg";
    }
    this.username = this.authService.getUsername();
  }

  // updateLogin(login : boolean): void {
  //   this.login.set(login);
  //   this.isLoggedIn = this.authService.isLoggedIn();
  //   this.username = this.authService.getUsername();
  // }
  isLoggedIn(): boolean {
    this.username = this.authService.getUsername();
    this.isLoggedInn = this.authService.isLoggedIn();
    return this.isLoggedInn;
  }
  
  logout(): void {
    this.authService.logout();
    window.location.reload();
  }
  onProfileClick(): void {
    this.sidebarService.toggleSidebar();
  }
}