import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { User } from '../interfaces/user';
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

  getUserDashboard(): Observable<User> {
    const storedUser = localStorage.getItem(this.jsonKey);
    if (storedUser) {
      return of(JSON.parse(storedUser));
    }
    return this.http.get<User>(this.jsonFilePath);
  }

  setUserDashboard(updatedUser: User): void {
    localStorage.setItem(this.jsonKey, JSON.stringify(updatedUser));
  }

  getUserDashboardLocal(): Observable<User> {
    return this.http.get<User>(this.jsonFilePath);
  }
}
