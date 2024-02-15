import { Component, ElementRef, ViewChild } from '@angular/core';
import { RoleUser, User, getRoleUser } from '../interfaces/user';
import { CompanyService } from '../services/company.service';
import { MatPaginator } from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import { UserService } from '../services/user.service';
import { Company } from '../interfaces/company';
import { rowsAnimation } from 'src/assets/animations';

@Component({
  selector: 'app-user-panel',
  templateUrl: './user-panel.component.html',
  styleUrls: ['./user-panel.component.css'],
  animations: [rowsAnimation],
})

export class UserPanelComponent {

  displayedColumns: string[] = ['id', 'name', 'email', 'phone', 'code', 'role', 'action'];
  dataSource = new MatTableDataSource<User>();
  company!: Company;
  selected = 'option2';
  token:string = ""
  me!:User;
  user:User|undefined;
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;
  clickedRows = new Set<User>();
  @ViewChild('notify') noty!: ElementRef;
  isLoading:Boolean = false
  constructor(private userService: UserService, private companyService: CompanyService) { }

  ngOnInit(): void {
    this.isLoading = true
    this.getCompanyByUser()
  }

  setUser(user:User){
    this.user = user
  }


  setRole(arg0: string) {
    this.user!.role = parseInt(arg0)
  }
  

  getCompanyByUser(): void {
    var stringT = sessionStorage.getItem('token')
    var stringU = sessionStorage.getItem('me')

    if(stringT && stringU){
      this.token = stringT;
      this.me = JSON.parse(stringU);
    }
    
    this.userService.getCompanyByUser(this.me, this.token)
    .subscribe(company => {
      setTimeout(() => {
        this.isLoading = false;
        this.company = company;
        this.loadEmployees();
      }, 1000);
      
    }, error => {
      console.error('Error getting company by user:', error.message); // Aquí se imprime solo el mensaje de error
    });
  }

  loadEmployees(): void {
    this.companyService.getEmployees(this.company, this.token)
      .subscribe(employees => {
        const users: User[] = [];

        employees.forEach((user, index) => {
          setTimeout(() => {
            users.push(user);
            this.dataSource.data = users;
          }, (index + 1) * 500); 
        });

        this.dataSource.paginator = this.paginator; 

      });
  }

  updateUser(user:User, email:HTMLInputElement, phone:HTMLInputElement){
   if( this.user!.role >0){
    this.isLoading = true
    var newUser:User = user
    newUser.phone = phone.value
    newUser.email = email.value
    newUser.role = this.user!.role 


    setTimeout(() => {
      this.userService.updateUser(newUser, this.token).subscribe(a=>{
        this.loadEmployees()
      })
      this.isLoading = false
      this.noty.nativeElement.click()
    }, 2500); 
   }

  }


  getRole(number:number|string |undefined){
    if(typeof number === 'undefined') return "unknow"
    let localNumber: number;

    if (typeof number === 'string') {
      localNumber = parseInt(number);
    } else {
      localNumber = number;
    }
  
    return getRoleUser(localNumber);
  }
}
