export interface UpdatePost {
    

    type: string;
    comment: string;
    like: boolean;
    username: string;
    title: string;
    content: string;
    categories: string[];
    visibility: string;
}