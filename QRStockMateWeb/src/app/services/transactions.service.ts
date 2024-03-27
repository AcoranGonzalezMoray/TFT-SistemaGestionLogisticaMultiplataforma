import { Injectable } from '@angular/core';
import { TransactionHistory } from '../interfaces/transaction-history';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from 'src/environment/environment';

@Injectable({
  providedIn: 'root'
})
export class TransactionsService {
  private apiUrl: string = environment.API + '/TransactionHistory';

  constructor(private http: HttpClient) { }

  getAll(): Observable<TransactionHistory[]> {
    return this.http.get<TransactionHistory[]>(this.apiUrl)
      .pipe(
        catchError(this.handleError)
      );
  }

  create(transactionHistory: TransactionHistory, token:string): Observable<TransactionHistory> {
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<TransactionHistory>(this.apiUrl, transactionHistory, {headers: headers})
      .pipe(
        catchError(this.handleError)
      );
  }

  update(transactionHistory: TransactionHistory): Observable<any> {
    return this.http.put<any>(this.apiUrl, transactionHistory)
      .pipe(
        catchError(this.handleError)
      );
  }

  delete(transactionHistory: TransactionHistory): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${transactionHistory.id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  getHistory(code: string, token:string): Observable<TransactionHistory[]> {
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<TransactionHistory[]>(`${this.apiUrl}/History/${code}`,{headers: headers})
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: any) {
    console.error('An error occurred:', error);
    return throwError(error);
  }
}
