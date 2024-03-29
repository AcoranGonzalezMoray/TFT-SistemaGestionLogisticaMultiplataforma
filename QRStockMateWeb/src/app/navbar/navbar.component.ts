import { Component } from '@angular/core';
import { me } from '../interfaces/transaction-history';
import { User } from '../interfaces/user';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  user: User = me()!;
}
