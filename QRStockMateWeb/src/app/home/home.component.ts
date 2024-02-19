import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { clearStorage } from '../sign-in/sign-in.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  activeItem: string = "User";
  create: boolean = false
  constructor( private router: Router) { 
    this.create = true
    setTimeout(() => {
      this.create = false
    }, 2000);

  }

  ngOnInit(): void {
    //Called after the constructor, initializing input properties, and the first call to ngOnChanges.
    //Add 'implements OnInit' to the class.
    if(!sessionStorage.getItem('me')){
      this.router.navigate(['/login']);
    }
  }


  finishAnimation() {
    this.create = false
  }


  logOut(){
    clearStorage()
    this.router.navigate(['/login']);
  }
  setActive(item: string) {
    this.activeItem = item
  }

}


