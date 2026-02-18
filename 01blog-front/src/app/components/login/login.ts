import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // ← ADD THIS
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/login-request';
import { NavbarComponent } from '../navbar/navbar';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {

  loginRequest: LoginRequest = {
    username: '',
    password: '',
  };
  

  errorMessage: string = '';
  isLoading: boolean = false;


  
  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    // if (this.authService.isLoggedIn()) {
    //   // this.router.navigate(['/home']);
    // }
  }

  // private navbarcom = inject(NavbarComponent); reje3ha service component standalone hna w f auth service
  
  onSubmit(): void {
    this.isLoading = true;
    // this.log.set(true);
    this.errorMessage = '';
    
    this.authService.login(this.loginRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.router.navigate(['/home']);
        // this.navbarcom.updateLogin(true);
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Login failed. Please check your credentials.';
      }
    });
      

  }
  ngAfterViewInit() {
  // Password visibility toggle
  const togglePassword = document.getElementById('togglePassword');
  const passwordInput = document.getElementById('password') as HTMLInputElement;
  
  if (togglePassword && passwordInput) {
    togglePassword.addEventListener('click', () => {
      const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
      passwordInput.setAttribute('type', type);
      
      const icon = togglePassword.querySelector('i');
      if (icon) {
        icon.classList.toggle('bi-eye');
        icon.classList.toggle('bi-eye-slash');
      }
    });
  }
}
}