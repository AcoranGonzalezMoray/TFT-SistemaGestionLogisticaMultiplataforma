import { Component } from '@angular/core';
import { Company } from '../interfaces/company';
import { User, getRoleUser } from '../interfaces/user';
import { UserService } from '../services/user.service';
import { CompanyService } from '../services/company.service';
import { getRoleStatus } from '../interfaces/transport-route';

@Component({
  selector: 'app-profile-panel',
  templateUrl: './profile-panel.component.html',
  styleUrls: ['./profile-panel.component.scss']
})
export class ProfilePanelComponent {
  company!: Company;
  token: string = ""
  me!: User;
  users:User[] = [];

  isLoading: Boolean = false
  constructor(private userService: UserService, private companyService: CompanyService) { }

  getRole(num:any){
    return getRoleUser(parseInt(num))
  }

  
  ngOnInit(): void {
    this.isLoading = true
    this.getCompanyByUser()
  }


  getCompanyByUser(): void {
    var stringT = sessionStorage.getItem('token')
    var stringU = sessionStorage.getItem('me')

    if (stringT && stringU) {
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
        this.users = employees;
      });
  }
}
