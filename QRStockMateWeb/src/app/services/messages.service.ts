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
  private apiUrl2: string = environment.APIv2 + '/Message';

  constructor(private http: HttpClient, private router: Router) { }

  getAllMessages(): Observable<Message[]> {

    return this.http.get<Message[]>(`${this.apiUrl}`);
  }

  createMessage(message: Message, token: string): Observable<Message> {
    // Configurar las cabeceras con JWT
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<Message>(`${this.apiUrl}`, message, { headers: headers });
  }

  updateMessage(message: Message): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}`, message);
  }

  deleteMessage(message: Message): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}`, { body: message });
  }

  getMessagesByCode(code: string, token: string): Observable<Message[]> {
    // Configurar las cabeceras con JWT
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });


    return this.http.get<Message[]>(`${this.apiUrl}/MessageByCode/${code}`, { headers: headers });
  }



  uploadFile(file: File, model: Message, token: string): Observable<void> {
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Accept': 'multipart/form-data' // Asegúrate de aceptar JSON
    });

    // No establezcas el Content-Type, déjalo que lo maneje el navegador

    const formData = new FormData();
    formData.append('file', file);
    formData.append('code', model.code.toString());
    formData.append('senderContactId', model.senderContactId.toString());
    formData.append('receiverContactId', model.receiverContactId.toString());
    formData.append('content', model.content);
    formData.append('sentDate', model.sentDate.toISOString());
    formData.append('type', model.type.toString());
    

    return this.http.post<void>(`${this.apiUrl}/UploadFile/`, formData, { headers: headers });
  }

  //"DeleteConversationByAngular/{param1}/{param2}"
  deleteConversation(param1: string, param2: string, token: string): Observable<void> {
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.delete<void>(`${this.apiUrl2}/DeleteConversation/${param1}/${param2}`, { headers: headers });
  }

  getNewMessage(format: string): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.apiUrl}/NewMessage/${format}`);
  }
}
