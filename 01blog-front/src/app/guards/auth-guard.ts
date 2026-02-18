import { CanActivateFn } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  // console.log("AuthGuard: Checking auloooooooooooooooooooooooooooool");
  return true;
};
