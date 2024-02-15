import { Component } from '@angular/core';
import { UserService } from '../services/user.service';
import { CompanyService } from '../services/company.service';
import { User } from '../interfaces/user';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css']
})
export class SignInComponent {
  token:string = ""
  me!:User;
  isLoading:Boolean = false

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
    this.userService.signIn(email, password)
      .subscribe(response => {
        this.isLoading=true

        setTimeout(() => {
          sessionStorage.setItem('token', response.token);
          sessionStorage.setItem('me', JSON.stringify(response.user));
        }, 1000);

        this.isLoading=false
        this.router.navigate(['']);
        
      }, error => {
        // Manejar cualquier error de autenticación
        console.error('Error signing in:', error);
      });
      
  }

}
