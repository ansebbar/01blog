import { Component, input, NgModule, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { PostRequest } from '../../models/post-request';
import { PostService } from '../../services/post-service';
import { GetPostsRequest } from '../../models/get-posts-request';
import { ProfileService } from '../../services/profile';
import { FormsModule } from '@angular/forms';
import { HomeService } from '../../services/home-service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent implements OnInit {
  username: string | null = '';
  isLoggedIn: boolean = false;
  user: any = null;
  allusers: any[] = [];
  allComments: any[] = [];
  allReports: any[] = [];
  searchchoice: string = 'posts';
  searchQuery: string = '';
  filterQuery: string = '';
  // searchedusers: any[] = [];
  // searchedposts: any[] = [];
  posts: any[] = [];
  postsprev: any[] = [];
  usersprev: any[] = [];
  mypst: boolean = false;
  adashbord: boolean = false;
  userProfilePicture: string | null = null;
  isLoading: boolean = true;
  constructor(private authService: AuthService,
              private router: Router, 
              private postService: PostService, 
              private profileService: ProfileService
            ) {}

  ngOnInit(): void {
    // console.log("HomeComponent initialized");
    this.isLoading = true;
    if (!localStorage.getItem('authToken') || !localStorage.getItem('username')) {
      this.isLoggedIn = false;
      this.isLoading = false;
      return;
    }

    this.authService.isLoggedIn().subscribe((isLoggedIn) => {
      this.isLoggedIn = isLoggedIn;
      this.mypst = false;
      console.log("isLoggedIn", this.isLoggedIn);
      if (this.isLoggedIn) {
        this.userProfilePicture = localStorage.getItem('userProfile');
        this.username = this.authService.getUsername();
        console.log("Logged in as:", this.username);
        this.profileService.fetchUserProfile().subscribe(user => {
          this.user = user;
          console.log("Fetched user profile:", this.user);
          this.postService.getAllPosts().subscribe(posts => {
          this.posts = posts;
          console.log("Fetched posts:", this.posts);
          // console.log("user id ",)
          this.profileService.getallusers(this.user.id).subscribe(users => {
          this.allusers = users;
          console.log("Fetched all users:", this.allusers);
          if(this.user.rateadmin === "admin" || this.user.rateadmin === "superadmin") {
          this.postService.getAllComments().subscribe(comments => {
          this.allComments = comments;
          console.log("Fetched all comments:", this.allComments);
        });
          }
          this.isLoading = false;
        });
        });
        });



      }else {
        this.isLoading = false;
      }
    });
  }



  setFilterType(type: string): void {
    if (this.searchchoice === type) {
      return; // No change, do nothing
    }
    this.searchchoice = type;
  }

  mypostslenght(): any[] {
    return this.posts.filter(post => post.creator === this.username);
  }
  myposts(): void {
    this.mypst = !this.mypst;
    if(this.mypst) {
      this.postsprev = this.posts;
      this.posts = this.posts.filter(post => post.creator == this.username);
    }else {
      this.posts = this.postsprev;
    }
    // this.posts = this.posts.filter(post => post.cre)
    // return this.posts.filter(post => post.creator === this.username);
  }

  //todo
  //////backend search algo needs to be implemented
  onSearch(): void {
        if (this.searchchoice === 'posts') {
      if (this.searchQuery.trim() === '') {
        if(this.postsprev.length > 0) {
          this.posts = this.postsprev;
          return;
        }
      } else {
        const query = this.searchQuery.toLowerCase();

        if(this.postsprev.length === 0) {
          this.postsprev = this.posts;
        
        this.posts = this.posts.filter(post =>
          post.title.toLowerCase().includes(query) ||
          post.content.toLowerCase().includes(query)
        );
      }else {
        this.posts = this.postsprev.filter(post =>
          post.title.toLowerCase().includes(query) ||
          post.content.toLowerCase().includes(query)
        );  
      }
    }
    } else if (this.searchchoice === 'users') {
      const query = this.searchQuery.toLowerCase();
      if (this.searchQuery.trim() === '') {
        if(this.usersprev.length > 0) {
          this.allusers = this.usersprev;
          return;
        }
      } else {
          if(this.usersprev.length === 0) {
        this.usersprev = this.allusers;
        this.allusers = this.allusers.filter(user =>
          user.username.toLowerCase().includes(query) ||
          user.firstName.toLowerCase().includes(query) ||
          user.lastName.toLowerCase().includes(query)
        );
      }else {
        this.allusers = this.usersprev.filter(user =>
          user.username.toLowerCase().includes(query) ||
          user.firstName.toLowerCase().includes(query) ||
          user.lastName.toLowerCase().includes(query)
        );
      }
    }
    }
  }    

  admin(): void {
    this.adashbord = !this.adashbord;
  }

  changestrate(id: number,type:string): void {
    this.profileService.dashboardchange(id,type).subscribe({
      next: () => {
        alert('Action performed successfully');
        // Optionally, refresh the data or update the UI here
      },
      error: (error) => {
        console.error('Failed to perform action:', error);
      },
    });
  }
  
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