import { Component, ElementRef, ViewChild } from '@angular/core';
import { Data } from '../interfaces/data';
import { DataService } from '../services/data.service';
import { GridItemServiceService } from '../services/grid-item-service.service';
import { Dashboard } from '../interfaces/dashboard';
import { Router } from '@angular/router';
@Component({
  selector: 'app-dashboards',
  templateUrl: './dashboards.component.html',
  styleUrls: ['./dashboards.component.css']
})
export class DashboardsComponent {


  userDashboard: Data| undefined;
  selectedDashboard: any; // Variable para almacenar el dashboard seleccionado
  @ViewChild('parentUl') parentUl: ElementRef | undefined;


  constructor(private userService: DataService, private gridItemService: GridItemServiceService, private router: Router) {}
  
  ngOnInit() {
    this.userService.getUserDashboard().subscribe(
      data => {
        this.userDashboard = data;
      },
      error => {
        console.error('Error al cargar el archivo JSON', error);
      }
    );
  
  }

  addDashboard(name: string) {
    // Crear un nuevo dashboard con el nombre proporcionado y un array vacío para 'vista'
    const newDashboard: Dashboard = { nombre: name, vista: [] };
  
    // Verificar si 'userDashboard' y 'userDashboard.data' existen
    if (this.userDashboard && this.userDashboard.data) {
      // Verificar si ya existe un dashboard con el mismo nombre
      const existingDashboard = this.userDashboard.data.dashboards.find(dashboard => dashboard.nombre === name);
  
      if (!existingDashboard) {
        // Si no existe, añadir el nuevo dashboard al array 'dashboards'
        this.userDashboard.data.dashboards.push(newDashboard);
  
        // Aquí puedes guardar los cambios en el servicio o donde lo necesites
        this.userService.setUserDashboard(this.userDashboard);
        console.log('Nuevo dashboard añadido con éxito');
      } else {
        console.log('Ya existe un dashboard con ese nombre');
      }
    }
  }
  

  open(dashboard:Dashboard){
    this.gridItemService.setDashboard(dashboard)
    this.selectedDashboard = dashboard
  }

  addHoverClass() {
   if(this.parentUl!=undefined){
    console.log("SIIII")
    this.parentUl.nativeElement.classList.add('active-list');
   }
  }

  removeHoverClass() {
    if(this.parentUl!=undefined){
      console.log("NOOO")

      this.parentUl.nativeElement.classList.remove('active-list');
    }
  }
}