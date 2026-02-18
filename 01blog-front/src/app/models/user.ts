export interface User {
  id: number;
  email: string;
  username: string;
  firstName: string;
  lastName: string;
  // displayname: string;
  createdAt: string;
  avatarUrl?: string;
  bio?: string;
  password?: string;
  followers: User[];     //services 
  following: User[];
  followersCount: number;
  followingCount: number;
  postsCount: number;
}