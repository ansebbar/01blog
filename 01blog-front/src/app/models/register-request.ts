export interface RegisterRequest {
  email: string;
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  display_name: string;
  avatarUrl?: string;
  bio?: string;
  dateOfBirth?: string;
}

//services