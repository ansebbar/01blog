export interface Profile {
//   id: number;
  username: string;
  firstName: string;
  lastName: string;
  createdAt: string;
  avatarUrl?: string;
  bio?: string;
  followers: Profile[];     //services 
  following: Profile[];
  followersCount: number;
  followingCount: number;
}