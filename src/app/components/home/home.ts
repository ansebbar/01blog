import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent implements OnInit {
  username: string | null = '';
  
  constructor(private authService: AuthService) {}
  
  ngOnInit(): void {
    this.username = this.authService.getUsername();
  }
  
  logout(): void {
    this.authService.logout();
  }
}