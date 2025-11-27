import { Component } from '@angular/core';
// import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // ← ADD THIS
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ProfileService } from '../../services/profile';
import { LoginRequest } from '../../models/login-request';
import { NavbarComponent } from '../navbar/navbar';
import { Auth } from '../../services/auth';
import { FpasswordRequest } from '../../models/fpassword-request';
@Component({
  selector: 'app-fpassword',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './fpassword.html',
  styleUrl: './fpassword.css',
})
export class Fpassword {

  fpassword: FpasswordRequest = {
    email: '',
    pin: null
  };
  
  
  errorMessage: string = '';
  isLoading: boolean = false;
  sended: boolean = false;
  
  constructor(private authService: AuthService, private profileService: ProfileService, private router: Router) {}
  
  onSubmit(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.fpassword.pin = this.authService.fpassword();
    this.sended = true;
    this.isLoading = false;
    this.router.navigate(['/fpassword']);
  }
  onPinSubmit(): void {
    if (this.authService.fpcheckpin(this.fpassword)) {
      this.profileService.changePassword(this.fpassword);
      alert('Password changed successfully');
      this.fpassword.pin = null;
      this.fpassword.email = '';
      this.router.navigate(['/login']);
    } else {
      this.errorMessage = 'Invalid PIN. Please try again.';
    }
  }
}
