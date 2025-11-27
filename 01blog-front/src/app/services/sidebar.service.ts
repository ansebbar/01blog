import { Injectable, signal, computed } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SidebarService {
  private _isOpen = signal(false);
  
  public isOpen = computed(() => this._isOpen());

  toggleSidebar() {
    console.log('Toggle sidebar called, current state:', this._isOpen());
    this._isOpen.update(value => !value);
  }

  openSidebar() {
    console.log('Opening sidebar');
    this._isOpen.set(true);
  }

  closeSidebar() {
    console.log('Closing sidebar');
    this._isOpen.set(false);
  }
}