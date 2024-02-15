import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  activeItem: string = "Dashboard";

  constructor( private router: Router) { }

  ngOnInit(): void {
    //Called after the constructor, initializing input properties, and the first call to ngOnChanges.
    //Add 'implements OnInit' to the class.
    if(!sessionStorage.getItem('me')){
      this.router.navigate(['/login']);
    }
  }

  logOut(){
    sessionStorage.clear()
    localStorage.clear()
    this.router.navigate(['/login']);
  }
  setActive(item: string) {
    this.activeItem = item
  }

}
