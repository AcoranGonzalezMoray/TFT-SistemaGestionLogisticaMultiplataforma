import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { environment } from 'src/environment/environment';
import { User } from '../interfaces/user';
import { Company } from '../interfaces/company';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl: string = environment.API + '/User';

  constructor(private http: HttpClient) { }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl)
      .pipe(
        catchError(error => {
          throw 'Error getting users: ' + error;
        })
      );
  }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, user)
      .pipe(
        catchError(error => {
          throw 'Error creating user: ' + error;
        })
      );
  }

  updateUser(user: User, token:string): Observable<void> {
    // Configurar las cabeceras con JWT
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.put<void>(this.apiUrl, user, {headers: headers})
      .pipe(
        catchError(error => {
          throw 'Error updating user: ' + error;
        })
      );
  }

  deleteUser(user: User): Observable<void> {
    return this.http.delete<void>(this.apiUrl, { body: user })
      .pipe(
        catchError(error => {
          throw 'Error deleting user: ' + error;
        })
      );
  }

  signIn(email: string, password: string): Observable<any> {
    const formData = new FormData();
    formData.append('email', email);
    formData.append('password', password);

    return this.http.post<any>(`${this.apiUrl}/SignIn`, formData)
      .pipe(
        catchError(error => {
          throw 'Error signing in: ' + error;
        })
      );
  }

  signUp(user: User, company: Company): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/SignUp`, { user, company })
      .pipe(
        catchError(error => {
          throw 'Error signing up: ' + error;
        })
      );
  }

  getCompanyByUser(user: User, token:string): Observable<Company> {
    // Configurar las cabeceras con JWT
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<Company>(`${this.apiUrl}/Company`, user, { headers: headers })
      .pipe(
        catchError(error => {
          throw 'Error getting company by user: ' + error;
        })
      );
  }

  deleteAccount(user: User): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/DeleteAccount`, { body: user })
      .pipe(
        catchError(error => {
          throw 'Error deleting account: ' + error;
        })
      );
  }

  updateImage(userId: number, image: File): Observable<void> {
    const formData = new FormData();
    formData.append('userId', userId.toString());
    formData.append('image', image);

    return this.http.post<void>(`${this.apiUrl}/UpdateImage`, formData)
      .pipe(
        catchError(error => {
          throw 'Error updating image: ' + error;
        })
      );
  }
}
