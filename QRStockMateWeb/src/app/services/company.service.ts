import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { environment } from 'src/environment/environment';
import { Company } from '../interfaces/company';
import { User } from '../interfaces/user';
import { Vehicle } from '../interfaces/vehicle';
import { Warehouse } from '../interfaces/warehouse';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {
  private apiUrl: string = environment.API + '/Company';

  constructor(private http: HttpClient) { }

  getCompanies(): Observable<Company[]> {
    return this.http.get<Company[]>(this.apiUrl)
      .pipe(
        catchError(error => {
          throw 'Error getting companies: ' + error;
        })
      );
  }

  createCompany(company: Company): Observable<Company> {
    return this.http.post<Company>(this.apiUrl, company)
      .pipe(
        catchError(error => {
          throw 'Error creating company: ' + error;
        })
      );
  }

  updateCompany(company: Company): Observable<void> {
    return this.http.put<void>(this.apiUrl, company)
      .pipe(
        catchError(error => {
          throw 'Error updating company: ' + error;
        })
      );
  }

  deleteCompany(company: Company): Observable<void> {
    return this.http.delete<void>(this.apiUrl, { body: company })
      .pipe(
        catchError(error => {
          throw 'Error deleting company: ' + error;
        })
      );
  }

  getEmployees(company: Company, token:string): Observable<User[]> {
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.post<User[]>(`${this.apiUrl}/Employees`, company, { headers: headers })
      .pipe(
        catchError(error => {
          throw 'Error getting employees: ' + error;
        })
      );
  }

  getVehicles(code: string): Observable<Vehicle[]> {
    return this.http.get<Vehicle[]>(`${this.apiUrl}/Vehicles/${code}`)
      .pipe(
        catchError(error => {
          throw 'Error getting vehicles: ' + error;
        })
      );
  }

  getWarehouses(company: Company): Observable<Warehouse[]> {
    return this.http.post<Warehouse[]>(`${this.apiUrl}/Warehouse`, company)
      .pipe(
        catchError(error => {
          throw 'Error getting warehouses: ' + error;
        })
      );
  }
}
