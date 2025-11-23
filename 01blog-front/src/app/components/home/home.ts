import { Component, input, NgModule, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { PostRequest } from '../../models/post-request';
import { PostService } from '../../services/post-service';
import { GetPostsRequest } from '../../models/get-posts-request';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent implements OnInit {
  username: string | null = '';
  isLoggedIn: boolean = false;
  posts: GetPostsRequest[] = [];
  constructor(private authService: AuthService, private router: Router, private postService: PostService) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    this.username = this.authService.getUsername();
    this.postService.getAllPosts().subscribe(posts => {
      this.posts = posts;
    });
  }

  


  //todo
  //////backend search algo needs to be implemented
  onSearch(): void {
    this.router.navigate(['/posts?search={}', input]);
  }
  getAllPosts(): void {}
    
  
  logout(): void {
    this.authService.logout();
  }
  login(): void {
    this.router.navigate(['/login']);
  }
  register(): void {
    this.router.navigate(['/register']);
  }
}