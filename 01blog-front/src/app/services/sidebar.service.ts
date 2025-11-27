import { Injectable, signal } from '@angular/core';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class SidebarService {
  public isOpen = signal(false);
  private username: string | null = null;

  constructor(private authService: AuthService) {
    this.username = this.authService.getUsername();
  }

  toggleSidebar() {
    this.isOpen.update(value => !value);
    console.log('Sidebar toggled. Current state:', this.isOpen());
  }

  openSidebar() {
    this.isOpen.set(true);
  }

  closeSidebar() {
    this.isOpen.set(false);
  }

  getSidebarState() {
    return this.isOpen.asReadonly();
  }
}
