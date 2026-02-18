import { CommentRequest } from "./comment-request";
import { PostImageRequest } from "./post-image-request";
import { Profile } from "./profile-request";
import { User } from "./user";

export interface GetPostsRequest {
    id ?: number;
    title: string;
    content: string;
    category: string;
    creator: Profile;
    dateFrom: string;
    updateDate: string;
    // images: PostImageRequest[];
    // comments: CommentRequest[];
    likes: string[];
    likeCount: number;
    commentsCount: number;
    visibility: string;
}

    //services and components
