import { PostImageRequest } from "./post-image-request";

export interface CreatePostRequest {
    title: string;
    content: string;
    creator: string;
    images: PostImageRequest[];
}
