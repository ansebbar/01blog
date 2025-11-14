import { CommentRequest } from "./comment-request";
import { PostImageRequest } from "./post-image-request";

export interface GetPostsRequest {
    title: string;
    content: string;
    creator: string;
    dateFrom: string;
    updateDate: string;
    images: PostImageRequest[];
    comments: CommentRequest[];
    likes: number;
    postLikesUsers: string[];
}
