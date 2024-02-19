import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { environment } from 'src/environment/environment';
import { TransportRoute } from '../interfaces/transport-route';

@Injectable({
  providedIn: 'root'
})
export class TransportRouteService {
  private apiUrl: string = environment.API + '/TransportRoute'

  constructor(private http: HttpClient) { }

  getAllTransportRoutes(): Observable<TransportRoute[]> {
    return this.http.get<TransportRoute[]>(this.apiUrl)
      .pipe(
         catchError(error => {
    console.error('An error occurred:', error);
    throw error;
  })
      );
  }

  getTransportRoutesByCode(code: string, token:string): Observable<TransportRoute[]> {
    const url = `${this.apiUrl}/TransportRoutes/${code}`;
        // Configurar las cabeceras con JWT
        let headers = new HttpHeaders({
          'Authorization': `Bearer ${token}`
        });
    return this.http.get<TransportRoute[]>(url, {headers: headers})
      .pipe(
         catchError(error => {
    console.error('An error occurred:', error);
    throw error;
  })
      );
  }

  getTransportRouteById(id: number): Observable<TransportRoute> {
    const url = `${this.apiUrl}/TransportRouteById/${id}`;
    return this.http.get<TransportRoute>(url)
      .pipe(
         catchError(error => {
    console.error('An error occurred:', error);
    throw error;
  })
      );
  }

  initRoute(id: number): Observable<Date> {
    const url = `${this.apiUrl}/InitRoute/${id}`;
    return this.http.put<Date>(url, null)
      .pipe(
         catchError(error => {
    console.error('An error occurred:', error);
    throw error;
  })
      );
  }

  finishRoute(id: number): Observable<Date> {
    const url = `${this.apiUrl}/FinishRoute/${id}`;
    return this.http.put<Date>(url, null)
      .pipe(
         catchError(error => {
    console.error('An error occurred:', error);
    throw error;
  })
      );
  }
}
