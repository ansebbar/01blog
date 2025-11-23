export interface PostRequest {
    title: string;
    content: string;
    categories: string[];
    creator: string;
    id ?: number;
}
// services and components