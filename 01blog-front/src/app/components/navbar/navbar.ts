// import { Component, inject, OnInit, signal, ViewChild } from '@angular/core';
// import { AuthService } from '../../services/auth.service';
// import { CommonModule } from '@angular/common';
// import { Router, RouterModule } from '@angular/router';
// // import { Sidebar } from '../sidebar/sidebar';
// import { SidebarService} from '../../services/sidebar.service';
// // import { LoginComponent } from '../login/login';
// import { HostListener } from '@angular/core';
// import { Navservice } from '../../services/navservice';


// @HostListener('window:scroll', ['$event'])
// @Component({
//   selector: 'app-navbar',
//   standalone: true,
//   imports: [CommonModule, RouterModule],
//   templateUrl: './navbar.html',
//   styleUrls: ['./navbar.css']
// })
// export class NavbarComponent implements OnInit {
//   isLoggedInn: boolean = false;
//   username: string | null = null;
//   profileImageUrl: string | null = null; // Default profile image path
//   // log = inject(AuthService);
//   //  @ViewChild(Sidebar) sidebarComponent!: Sidebar;
//   private authService = inject(AuthService);
//   private sidebarService = inject(SidebarService);
//   // private navservice = inject(Navservice);
//   ngOnChanges(): void {
//         if (this.isLoggedIn()) {
//           this.isLoggedInn = true;
//       this.username = this.authService.getUsername();
//       this.profileImageUrl = this.authService.getProfileImageUrl();
//       // console.log("Profile image URL in navbar:", this.profileImageUrl);
//       // if (!this.profileImageUrl || this.profileImageUrl.trim() === '') {
//       //   this.profileImageUrl =  "https://i.pinimg.com/474x/18/b9/ff/18b9ffb2a8a791d50213a9d595c4dd52.jpg";
//       // }
//     }else {
//       this.isLoggedInn = false;
//     }
//   }
//   ngOnInit(): void {
//     // this.isLoggedInn = this.authService.isLoggedIn();
//     // this.isLoggedIn();
//     // console.log("Navbar initialized. Checking login status...");
//     // if () {
//     this.isLoggedInn = this.isLoggedIn();
//       if (this.isLoggedInn) {
//         this.profileImageUrl = this.authService.getProfileImageUrl();
//         this.username = this.authService.getUsername();
//       }
//       // console.log("Profile image URL in navbar:", this.profileImageUrl);
//       // if (!this.profileImageUrl || this.profileImageUrl.trim() === '') {
//         // this.profileImageUrl =  "https://i.pinimg.com/474x/18/b9/ff/18b9ffb2a8a791d50213a9d595c4dd52.jpg";
//       // }
//     // }
    
//   }

//   updateLogin(login : boolean): void {
//     // this.login.set(login);
//     this.isLoggedInn = login;
//     // this.username =;
//     // if (login && this.authService.getProfileImageUrl())
//       this.profileImageUrl = this.authService.getProfileImageUrl();
//   }
//   isLoggedIn(): boolean {
//     console.log("Checking login status in NavbarComponent...",this.authService.getToken());
//     if (this.authService.getToken() != null) {
//       // this.isLoggedInn = this.authService.isLoggedIn();
      
//       this.authService.isLoggedIn().subscribe((isValid) => {
//       if (!isValid) {
//         this.isLoggedInn = false;
//         console.log("Token invalid, logging out...");
//           this.logout();
//           return false;
//       }else {
//         this.isLoggedInn = true;
//         console.log("Token valid, user is logged in.");
//         return this.isLoggedInn;
//       }
//       // return this.isLoggedInn;
//     });
//     // console.log("fdsfsdf",this.isLoggedInn)
//     // this.isLoggedInn = false;
//     // return this.isLoggedInn;
//   }
//   this.isLoggedInn = false;
//   return this.isLoggedInn;
// }
  
//   logout(): void {
//     this.authService.logout();
//     // window.location.reload();
//   }
//   onProfileClick(): void {
//     this.sidebarService.toggleSidebar();
//   }

  
//   onWindowScroll() {
//   const navbar = document.querySelector('.navbar');
//   if (window.scrollY > 50) {
//     navbar?.classList.add('scrolled');
//   } else {
//     navbar?.classList.remove('scrolled');
//   }
// }
// }

import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { SidebarService} from '../../services/sidebar.service';
import { HostListener } from '@angular/core';

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
  profileImageUrl: string | null = null;
  unreadNotificationCount: number = 0;
  showNotifications: boolean = false;
  notifications: Notification[] = [];


  
  private authService = inject(AuthService);
  private sidebarService = inject(SidebarService);
  
  ngOnInit(): void {
    this.checkAuthStatus();
  }
  
  checkAuthStatus(): void {
    const token = this.authService.getToken();
    
    if (token) {
      this.authService.isLoggedIn().subscribe({
        next: (isValid) => {
          if (isValid) {
            this.isLoggedInn = true;
            this.username = this.authService.getUsername();
            this.profileImageUrl = this.authService.getProfileImageUrl();
          } else {
            this.clearUserData();
          }
        },
        error: () => {
          this.clearUserData();
        }
      });
    } else {
      this.clearUserData();
    }
  }

  isloggedIn(): boolean {
    if (this.authService.getToken() != null) {
      this.isLoggedInn = true;
      this.username = this.authService.getUsername();
      this.profileImageUrl = this.authService.getProfileImageUrl();
      return true;
    }
    this.isLoggedInn = false;
    return false;
  }
  
  private clearUserData(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('username');
    // localStorage.removeItem('email');
    localStorage.removeItem('userProfile');
    this.isLoggedInn = false;
    this.username = null;
    this.profileImageUrl = null;
  }
  
  logout(): void {
    this.authService.logout();
    this.clearUserData();
  }
  
  onProfileClick(): void {
    this.sidebarService.toggleSidebar();
  }
  
  @HostListener('window:scroll', [])
  onWindowScroll() {
    const navbar = document.querySelector('.navbar');
    if (window.scrollY > 50) {
      navbar?.classList.add('scrolled');
    } else {
      navbar?.classList.remove('scrolled');
    }
  }
}