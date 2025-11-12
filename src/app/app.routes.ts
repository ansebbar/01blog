import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { HomeComponent } from './components/home/home';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  // Public routes - anyone can access
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  
  // Protected routes - require login
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  
  // Default route - redirect to home
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  
  // Wildcard route - redirect to home for any unknown routes
  { path: '**', redirectTo: '/home' }
];