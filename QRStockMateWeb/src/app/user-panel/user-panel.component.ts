import { Component, ViewChild } from '@angular/core';
import { RoleUser, User, getRoleUser } from '../interfaces/user';
import { CompanyService } from '../services/company.service';
import { MatPaginator } from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import { UserService } from '../services/user.service';
import { Company } from '../interfaces/company';
@Component({
  selector: 'app-user-panel',
  templateUrl: './user-panel.component.html',
  styleUrls: ['./user-panel.component.css']
})
export class UserPanelComponent {
  displayedColumns: string[] = ['id', 'name', 'email', 'phone', 'code', 'role', 'action'];
  dataSource = new MatTableDataSource<User>();

  company!: Company;
  token:string = ""
  me!:User;
  user:User|undefined;
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;
  clickedRows = new Set<User>();

  constructor(private userService: UserService, private companyService: CompanyService) { }

  ngOnInit(): void {
    
    this.signIn("acoran@gmail.com", "123")
    

  }

  setUser(user:User){
    this.user = user
  }

  signIn(email: string, password: string): void {
    this.userService.signIn(email, password)
      .subscribe(response => {
        sessionStorage.setItem('token', response.token);
        sessionStorage.setItem('me', JSON.stringify(response.user));

        var stringT = sessionStorage.getItem('token')
        var stringU = sessionStorage.getItem('me')

        if(stringT && stringU){
          this.token = stringT;
          this.me = JSON.parse(stringU);
          this.getCompanyByUser()
        }

      }, error => {
        // Manejar cualquier error de autenticación
        console.error('Error signing in:', error);
      });
  }

  getCompanyByUser(): void {

    this.userService.getCompanyByUser(this.me, this.token)
    .subscribe(company => {
      this.company = company;
      this.loadEmployees();
    }, error => {
      console.error('Error getting company by user:', error.message); // Aquí se imprime solo el mensaje de error
    });
  }

  loadEmployees(): void {
    // Llamar al método getEmployees del servicio CompanyService para obtener la lista de empleados
    this.companyService.getEmployees(this.company, this.token)
      .subscribe(employees => {
        this.dataSource.data = employees; // Establecer la lista de empleados en el dataSource
        this.dataSource.paginator = this.paginator; // Asignar el paginador al dataSource
        console.log(employees)
      });
  }


  getRole(number:number){
    return getRoleUser(number)
  }
}
