import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { PostRequest } from '../models/post-request';
import { UpdatePost } from '../models/update-post';

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private apiUrl = 'http://localhost:8080/api/posts';

  constructor(private http: HttpClient) { }

  createPost(formData: PostRequest): Promise<boolean> {
    console.log("Creating post with data:", formData);
    return this.http.post(`${this.apiUrl}/newpost`, formData).pipe(
      map((response: any) => {
        console.log('Post created successfully:', response);
        return response;
      })
    ).toPromise() as Promise<boolean>;
  }

  getAllPosts(): Observable<any> {
    return this.http.get(this.apiUrl);
  }
  getPostById(postId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${postId}`);
  }
  getAllComments(): Observable<any> {
    return this.http.get(`${this.apiUrl}/allcomments`);
  }

  updatePost(postId: number, formData: UpdatePost): Observable<any> {
    console.log("Updating post with ID:", postId, "and data:", formData);
    return this.http.put(`${this.apiUrl}/${postId}`, formData);
  }

  deletePost(postId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${postId}`,{ responseType: 'text' });
  }

  likeComment(id: number, username: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/comment/like?id=${id}&username=${username}`, {});
  }

  dislikeComment(id: number, username: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/comment/dislike?id=${id}&username=${username}`, {});
  }
  reportComment(id: number, username: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/comment/report?id=${id}&username=${username}`,{}, { responseType: 'text' });
  }

  reportPost(postId: number, reason: string): Observable<any> {//backend endpoint katessenak
    console.log("Reporting post with ID:", postId, "for reason:", reason);
    return this.http.post(`${this.apiUrl}/post/report`, { username: localStorage.getItem('username'), raison: reason, reportedpostId: postId },{ responseType: 'text' });
  }
  deleteComment(commentId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/comment/delete?id=${commentId}`,{ responseType: 'text' });
  }
}
