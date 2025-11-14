export interface User {
  id: number;
  email: string;
  username: string;
  firstName: string;
  lastName: string;
  createdAt: string;
  avatarUrl?: string;
  bio?: string;
  followers: Map<string, string>;     //services 
  following: Map<string, string>;
}