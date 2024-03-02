import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { environment } from 'src/environment/environment';
import { Vehicle } from '../interfaces/vehicle';

@Injectable({
  providedIn: 'root'
})
export class VehicleService {

  private apiUrl: string = environment.API + '/Vehicle';

  constructor(private http: HttpClient) { }

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`)
      .pipe(
        
      );
  }

  create(vehicle: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}`, vehicle)
      .pipe(
        
      );
  }

  update(vehicle: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}`, vehicle)
      .pipe(
        
      );
  }

  delete(vehicle: any): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}`, { body: vehicle })
      .pipe(
        
      );
  }

  updateLocation(id: number, location: string): Observable<any> {
    
    return this.http.put<any>(`${this.apiUrl}/UpdateLocation/${id}`, `"${location}"`)
      .pipe(
        
      );
  }

  getLocation(id: number, token:string): Observable<Vehicle> {
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<any>(`${this.apiUrl}/GetLocation/${id}`, {headers: headers})
      .pipe(
        
      );
  }

  private handleError(error: any) {
    console.error('An error occurred:', error);
    throw error;
  }
}
