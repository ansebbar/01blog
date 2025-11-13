import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { HomeComponent } from './components/home/home';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  
  { path: 'home', component: HomeComponent},
  
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  
  { path: '**', redirectTo: '/home' }
];