
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface SupportRequest {
  name: string;
  email: string;
  subject: string;
  message: string;
  category: string;
}

export interface ApiResponse {
  success: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class SupportService {
  private apiUrl = environment.apiUrl; // Update with your backend URL

  constructor(private http: HttpClient) {}

  sendSupportRequest(request: SupportRequest): Observable<ApiResponse> {
    console.log("Sending support request:", request);
    return this.http.post<ApiResponse>(`${this.apiUrl}/support/contact`, request);
  }
}