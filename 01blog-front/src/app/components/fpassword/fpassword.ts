import { Component } from '@angular/core';
// import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // ← ADD THIS
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ProfileService } from '../../services/profile';
import { LoginRequest } from '../../models/login-request';
import { NavbarComponent } from '../navbar/navbar';
import { FpasswordRequest } from '../../models/fpassword-request';
@Component({
  selector: 'app-fpassword',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './fpassword.html',
  styleUrls: ['./fpassword.css'],
})
export class Fpassword {

  fpassword: FpasswordRequest = {
    email: '',
    pin: null
  };
  
  
  errorMessage: string = '';
  isLoading: boolean = false;
  sended: boolean = false;
  pinDigits: string[] = ['', '', '', '', '', ''];
  resendCooldown = 0;
  private cooldownInterval: any;
  
  constructor(private authService: AuthService, private profileService: ProfileService, private router: Router) {}
  
  onSubmit(): void {
    this.isLoading = true;
    this.errorMessage = '';
    if (!this.authService.fpassword(this.fpassword.email)) {
      console.log('PIN sent successfully');
      this.sended = true;
      this.isLoading = false;
      this.errorMessage = '';
      this.router.navigate(['/fpassword']);
    } else {
      this.errorMessage = 'Error sending email not found. Please try again.';
      this.sended = false;
      this.isLoading = false;
    }
  }
  onPinSubmit(): void {
    this.fpassword.pin = this.pinDigits.join('');
    if (this.authService.pinVerify(this.fpassword.email, this.fpassword.pin)) {
      this.profileService.changePassword(this.fpassword);
      alert('Password changed successfully');
      this.fpassword.pin = null;
      // this.fpassword.email = '';
      // this.router.navigate(['/login']);
    } else {
      this.errorMessage = 'Invalid PIN. Please try again.';
    }
  }
  // Add these to your component

  
  // Move to next input on digit entry
  moveToNext(index: number, event: any) {
    const input = event.target;
    const value = input.value;
    
    if (value && index < 5) {
      const nextInput = document.querySelectorAll('.otp-input')[index + 1] as HTMLInputElement;
      nextInput?.focus();
      nextInput?.select();
    }
    
    this.updatePinValue();
  }
  
  moveToPrev(index: number, event: any) {
    if (event.key === 'Backspace' && !this.pinDigits[index] && index > 0) {
      const prevInput = document.querySelectorAll('.otp-input')[index - 1] as HTMLInputElement;
      prevInput?.focus();
      prevInput?.select();
    }
    this.updatePinValue();
  }
  
  updatePinValue() {
    this.fpassword.pin = this.pinDigits.join('');
  }
  
  isPinComplete(): boolean {
    return this.pinDigits.every(digit => digit.length === 1);
  }
  
  resendPin() {
    if (this.resendCooldown > 0) return;
    
    this.resendCooldown = 60;
    
    this.cooldownInterval = setInterval(() => {
      this.resendCooldown--;
      if (this.resendCooldown <= 0) {
        clearInterval(this.cooldownInterval);
      }
    }, 1000);
    this.authService.fpassword(this.fpassword.email);
  }
  
  changeEmail() {
    this.sended = false;
    this.pinDigits = ['', '', '', '', '', ''];
    this.fpassword.pin = '';
  }
  
  // Clean up interval on destroy
  ngOnDestroy() {
    if (this.cooldownInterval) {
      clearInterval(this.cooldownInterval);
    }
  }
}
