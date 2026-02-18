import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  
  constructor(private authService: AuthService, private router: Router) {}
  

canActivate(): Observable<boolean> {
  // Check localStorage first
  if (!localStorage.getItem("authToken") || !localStorage.getItem("username")) {
    this.router.navigate(['/home']);
    return of(false); 
  }
  
  // Return the observable from the service
  return this.authService.isLoggedIn().pipe(
    map((res) => {
      if (res) {
        return true;
      } else {
        this.router.navigate(['/home']);
        return false;
      }
    })
  );
}
}