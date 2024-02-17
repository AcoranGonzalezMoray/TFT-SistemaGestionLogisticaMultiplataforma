import { Component, ElementRef, ViewChild } from '@angular/core';
import { Warehouse } from '../interfaces/warehouse';
import { MatTableDataSource } from '@angular/material/table';
import { User } from '../interfaces/user';
import { MatPaginator } from '@angular/material/paginator';
import { CompanyService } from '../services/company.service';
import { UserService } from '../services/user.service';
import { Company } from '../interfaces/company';
import { rowsAnimation } from 'src/assets/animations';


@Component({
  selector: 'app-warehouse-panel',
  templateUrl: './warehouse-panel.component.html',
  styleUrls: ['./warehouse-panel.component.css'],
  animations: [rowsAnimation],
})

export class WarehousePanelComponent {

  displayedColumns: string[] = ['id', 'name', 'administrator', 'location', 'organization','numº Item',  'action'];
  dataSource = new MatTableDataSource<Warehouse>();
  token:string = ""
  warehouse:Warehouse|undefined;
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;
  clickedRows = new Set<Warehouse>();
  @ViewChild('notify') noty!: ElementRef;
  @ViewChild('notifEmpty') notE!: ElementRef;
  isLoading:Boolean = false

  company!: Company;

  constructor(private companyService: CompanyService, private userService:UserService) { }

  ngOnInit(): void {
    this.isLoading = true
    this.getCompanyByUser()
  }

  setWarehouse(warehouse:Warehouse){
    this.warehouse = warehouse
  }

  getCompanyByUser(): void {
    var stringT = sessionStorage.getItem('token')
    var stringU = sessionStorage.getItem('me')
    var user:User;
    if(stringT && stringU){
      this.token = stringT;
      user  = JSON.parse(stringU);
    }
    
    this.userService.getCompanyByUser(user!, this.token)
    .subscribe(company => {
      setTimeout(() => {
        this.isLoading = false;
        this.company = company;
        this.loadWarehouse();
      }, 1000);
      
    }, error => {
      console.error('Error getting company by user:', error.message); // Aquí se imprime solo el mensaje de error
    });
  }
  searchByValue(element:HTMLInputElement){
    this.dataSource.filter = element.value.trim().toLowerCase();

  }
  
  loadWarehouse(): void {
    this.companyService.getWarehouses(this.company, this.token)
      .subscribe(warehousesNew => {
        const warehouses: Warehouse[] = [];


        warehousesNew.forEach((w, index) => {
          setTimeout(() => {
            warehouses.push(w);
            this.dataSource.data = warehouses;
          }, (index + 1) * 500); 
        });


        this.dataSource.paginator = this.paginator; 

      }, error => {
        this.notE.nativeElement.click()
      });
  }
}
