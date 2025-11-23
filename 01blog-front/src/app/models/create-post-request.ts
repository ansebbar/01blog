import { PostImageRequest } from "./post-image-request";

export interface CreatePostRequest {
    title: string;
    content: string;
    categories: string[];
    creator: string;
    images: PostImageRequest[];
}

    //services and components
