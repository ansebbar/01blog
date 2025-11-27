import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarService } from '../../services/sidebar.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.css']
})


//todo sidebar component to show profile info and options hnaaaaa a anas
export class SidebarComponent {
  sidebarService = inject(SidebarService);
  authService = inject(AuthService);
  username: string | null = null;
  profileImageUrl: string = '';
  
  ngOnInit(): void {
    this.username = this.authService.getUsername();
    this.profileImageUrl = this.authService.getProfileImageUrl() || "https://i.pinimg.com/474x/18/b9/ff/18b9ffb2a8a791d50213a9d595c4dd52.jpg";
  }
}