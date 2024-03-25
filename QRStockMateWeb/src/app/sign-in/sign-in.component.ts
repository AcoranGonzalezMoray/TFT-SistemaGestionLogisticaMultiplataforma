import { Component, ElementRef, ViewChild } from '@angular/core';
import { UserService } from '../services/user.service';
import { CompanyService } from '../services/company.service';
import { User, getRoleUser } from '../interfaces/user';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css']
})
export class SignInComponent {
  token: string = ""
  me!: User;
  isLoading: Boolean = false
  @ViewChild('notifyError') notyE!: ElementRef;
  @ViewChild('notifyOk') notyS!: ElementRef;
  @ViewChild('notifyRole') notyR!: ElementRef;
  username: string = '';
  password: string = '';

  constructor(private userService: UserService, private router: Router) { }


  onInput(inp: string): void {
    if (inp === 'usr') {
      document.getElementById("email")?.focus();
    } else if (inp === 'pass') {
      document.getElementById("password")?.focus();
    } else {
      document.getElementById("email")?.focus();
    }
  }

  signIn(email: string, password: string): void {
    this.isLoading = true

    this.userService.signIn(email, password)
      .subscribe(response => {

        setTimeout(() => {
          var user: User = response.user

          if (user.role == 0 || user.role == 1) {
            sessionStorage.setItem('token', response.token);
            sessionStorage.setItem('me', JSON.stringify(response.user));
            this.notyS.nativeElement.click()
            this.router.navigate(['']);
          } else {
            this.notyR.nativeElement.click()
          }
          this.isLoading = false
        }, 2000);


      }, error => {
        // Manejar cualquier error de autenticación
        console.error('Error signing in:', error);
        this.notyE.nativeElement.click()
        this.isLoading = false;
      });

  }

}

export function clearStorage() {
  sessionStorage.clear()
  localStorage.clear()
  console.log("limpio")
}