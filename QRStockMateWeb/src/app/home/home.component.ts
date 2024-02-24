import { Component, ElementRef, ViewChild} from '@angular/core';
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

  
  mainCoordinates = { top: 258.344, left: 240,height: 72, width: 0};

  calculateMainPosition(activeElement: HTMLElement) {
    const containerRect = activeElement.getBoundingClientRect();
    this.mainCoordinates.height = 72
    this.mainCoordinates.top = (containerRect.top + containerRect.height / 2)-36;
    this.mainCoordinates.left = (containerRect.left + containerRect.width / 2)+115;
  }

  @ViewChild("DSH") li!:ElementRef;
  calculateMainPositionDrop() {
    
    const containerRect =  this.li.nativeElement.getBoundingClientRect();
    this.mainCoordinates.height =  containerRect.height +20
    this.mainCoordinates.top = 125
    this.mainCoordinates.left = (containerRect.left + containerRect.width / 2)+115;
  }

  @ViewChild("DSB") li2!:ElementRef;
  calculateMainPositionDropAj(h?:number) {

    const containerRect =  this.li2.nativeElement.getBoundingClientRect();
    this.mainCoordinates.height =  containerRect.height +20
    console.log( containerRect.height)
    this.mainCoordinates.top = h?h:(containerRect.top + containerRect.height / 2)-87;
    this.mainCoordinates.left = (containerRect.left + containerRect.width / 2)+115;
  }

  ngOnInit(): void {
    if(!sessionStorage.getItem('me')){
      //this.router.navigate(['/login']);
    }
    
  }
  

  finishAnimation() {
    this.create = false
  }


  logOut(){
    clearStorage()
    this.router.navigate(['/login']);
  }

  drop() {
    setTimeout(()=>{
      const activeElement = document.querySelector('.active');
      if (activeElement && this.activeItem != "Profile"  && this.activeItem != "Dashboard") {
        (activeElement as HTMLElement).click();
        return;
      }
      if (activeElement && this.activeItem == "Profile") {
        this.calculateMainPositionDropAj(177)
        return;
      }
      if (activeElement && this.activeItem == "Dashboard") {
        this.calculateMainPositionDrop()
        return;
      }
    },350)
  }

  setActive(item: string, event:any) {
    this.activeItem = item
    if(this.activeItem == "Dashboard"){
      this.calculateMainPositionDrop()
      return;
    }
    if(this.activeItem == "Profile"){
      this.calculateMainPositionDropAj()
    }else {
      this.calculateMainPosition(event)
    }
  }

}


