import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class Navservice {



  private loggedIn: boolean = false;

  constructor() {}

  setLoginStatus(status: boolean): void {
    this.loggedIn = status;
  }

  getLoginStatus(): boolean {
    return this.loggedIn;
  }
  
}
