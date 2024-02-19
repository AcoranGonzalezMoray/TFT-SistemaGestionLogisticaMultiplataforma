import { Component, ElementRef, ViewChild } from '@angular/core';
import { Vehicle } from '../interfaces/vehicle';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { CompanyService } from '../services/company.service';
import { UserService } from '../services/user.service';
import { User } from '../interfaces/user';
import { rowsAnimation } from 'src/assets/animations';

@Component({
  selector: 'app-vehicle-panel',
  templateUrl: './vehicle-panel.component.html',
  styleUrls: ['./vehicle-panel.component.css'],
  animations: [rowsAnimation]
})
export class VehiclePanelComponent {
  displayedColumns: string[] = ['id', 'code', 'make', 'model', 'year', 'color', 'licensePlate', 'maxLoad', 'action'];
  dataSource = new MatTableDataSource<Vehicle>();
  token:string = ""
  vehicle:Vehicle|undefined;
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;
  clickedRows = new Set<Vehicle>();
  @ViewChild('notifyV') noty!: ElementRef;
  @ViewChild('notifEmptyV') notE!: ElementRef;
  isLoading:Boolean = false
  code: string = "";

  constructor(private companyService: CompanyService, private userService:UserService) { }

  ngOnInit(): void {
    this.isLoading = true
    this.loadVehicles();
    
  }

  setVehicle(vehicle:Vehicle){
    this.vehicle = vehicle
  }

  searchByValue(element:HTMLInputElement){
    this.dataSource.filter = element.value.trim().toLowerCase();

  }

  loadVehicles(): void {
    var stringT = sessionStorage.getItem('token')
    var stringM = sessionStorage.getItem('me')
    if (stringT && stringM){
      this.token = stringT
      var user:User = JSON.parse(stringM)
      this.code = user.code;

    }
    
    this.companyService.getVehicles(this.code,this.token)
      .subscribe(vehicles => {
        if(vehicles.length == 0){
          setTimeout(() => {
            this.isLoading = false
            this.notE.nativeElement.click()
          }, 1000);
        }else {
          const v: Vehicle[] = [];

       
          vehicles.forEach((i: Vehicle, index: number) => {
            setTimeout(() => {
              v.push(i);
              this.dataSource.data = v;
            }, (index + 1) * 500); 
          });
          setTimeout(() => {
            this.isLoading = false
          }, 1000);
          this.dataSource.paginator = this.paginator; 
        }
        
        
      }, error => {
        setTimeout(() => {
          this.isLoading = false
          this.notE.nativeElement.click()
        }, 1000);
      });
  }
}
