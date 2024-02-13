import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Data } from '../interfaces/data';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private jsonKey = 'userDashboard'; 
  private jsonFilePath = 'assets/user-dashboard.json'; 

  constructor(private http: HttpClient) {
    const storedUser = localStorage.getItem(this.jsonKey);
    if (!storedUser) {
      this.getUserDashboardLocal().subscribe((user) => {
        this.setUserDashboard(user);
      });
    }
  }

  getUserDashboard(): Observable<Data> {
    const storedUser = localStorage.getItem(this.jsonKey);
    if (storedUser) {
      return of(JSON.parse(storedUser));
    }
    return this.http.get<Data>(this.jsonFilePath);
  }

  setUserDashboard(updatedUser: Data): void {
    localStorage.setItem(this.jsonKey, JSON.stringify(updatedUser));
  }

  getUserDashboardLocal(): Observable<Data> {
    return this.http.get<Data>(this.jsonFilePath);
  }
}
