import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message } from '../interfaces/message';
import { Router } from '@angular/router';
import { environment } from 'src/environment/environment';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private apiUrl: string = environment.API + '/Message';

  constructor(private http: HttpClient,private router: Router) { }

  getAllMessages(): Observable<Message[]> {
    
    return this.http.get<Message[]>(`${this.apiUrl}`);
  }

  createMessage(message: Message, token:string): Observable<Message> {
        // Configurar las cabeceras con JWT
        let headers = new HttpHeaders({
          'Authorization': `Bearer ${token}`
        });
    
    return this.http.post<Message>(`${this.apiUrl}`, message, {headers: headers});
  }

  updateMessage(message: Message): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}`, message);
  }

  deleteMessage(message: Message): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}`, { body: message });
  }

  getMessagesByCode(code: string, token:string): Observable<Message[]> {
    // Configurar las cabeceras con JWT
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });


    return this.http.get<Message[]>(`${this.apiUrl}/MessageByCode/${code}`, {headers: headers});
  }

  uploadFile(file: File, model: Message): Observable<void> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('model', JSON.stringify(model));
    return this.http.post<void>(`${this.apiUrl}/UploadFile/`, formData);
  }

  deleteConversation(user: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/DeleteConversation`, { body: user });
  }

  getNewMessage(format: string): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.apiUrl}/NewMessage/${format}`);
  }
}
