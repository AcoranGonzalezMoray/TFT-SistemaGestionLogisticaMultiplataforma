import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from 'src/environment/environment';
import { Communication } from '../interfaces/communication';

@Injectable({
  providedIn: 'root'
})
export class CommunicationService {
  private baseUrl: string = environment.API + '/Communication';

  constructor(private http: HttpClient) { }

  getAllCommunications(): Observable<Communication[]> {
    return this.http.get<Communication[]>(this.baseUrl)
      .pipe(
        catchError(this.handleError)
      );
  }

  getCommunicationsByCode(code: string, token:string): Observable<Communication[]> {
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<Communication[]>(`${this.baseUrl}/GetByCode/${code}`, {headers: headers})
      .pipe(
        catchError(this.handleError)
      );
  }

  createCommunication(communication: Communication, token:string): Observable<Communication> {
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.post<Communication>(this.baseUrl, communication, {headers:headers})
      .pipe(
        catchError(this.handleError)
      );
  }

  updateCommunication(communication: Communication): Observable<void> {
    return this.http.put<void>(this.baseUrl, communication)
      .pipe(
        catchError(this.handleError)
      );
  }

  deleteCommunication(communication: Communication): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${communication.id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: any) {
    console.error('Error:', error);
    return throwError('Something went wrong; please try again later.');
  }
}
