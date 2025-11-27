import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { HomeComponent } from './components/home/home';
import { AuthGuard } from './guards/auth.guard';
import { ProfileComponent } from './components/profile/profile';
import { PostCreateComponent } from './components/post-creation/post-creation';
import { Fpassword } from './components/fpassword/fpassword';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'fpassword', component: Fpassword},
  { path: 'register', component: RegisterComponent },
  { path: 'post-creation', component: PostCreateComponent, canActivate: [AuthGuard] },
  
  { path: 'home', component: HomeComponent, /*canActivate: [AuthGuard]*/ },
  { path: 'profile', component:ProfileComponent , canActivate: [AuthGuard] },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  
  { path: '**', redirectTo: '/home' }
];