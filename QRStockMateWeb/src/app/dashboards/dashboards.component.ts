import { Component, ElementRef, ViewChild } from '@angular/core';
import { Data } from '../interfaces/data';
import { DataService } from '../services/data.service';
import { GridItemServiceService } from '../services/grid-item-service.service';
import { Dashboard, MapDash } from '../interfaces/dashboard';
import { Router } from '@angular/router';
@Component({
  selector: 'app-dashboards',
  templateUrl: './dashboards.component.html',
  styleUrls: ['./dashboards.component.css']
})
export class DashboardsComponent {


  userDashboard: Data | undefined;
  selectedDashboard: any; // Variable para almacenar el dashboard seleccionado
  @ViewChild('parentUl') parentUl: ElementRef | undefined;
  @ViewChild('notifyValidName') validName!: ElementRef;
  @ViewChild('notifyErrorDash') errorDash!: ElementRef;
  @ViewChild('notifyOkDash') okDash!: ElementRef;

  constructor(private userService: DataService, private gridItemService: GridItemServiceService, private router: Router) { }

  ngOnInit() {
    this.loadDashboard()
  }
  loadDashboard(open? : boolean, dashboard?:Dashboard) {
    this.userService.getUserDashboard().subscribe(
      data => {
        this.userDashboard = data;

        if(open){
          var tmp = data.data.dashboards.find((d) => d.nombre == dashboard?.nombre)
          const dash =tmp?tmp:dashboard!;
          this.gridItemService.setDashboard(dash)
          this.selectedDashboard = dash
        }

      },
      error => {
        console.error('Error al cargar el archivo JSON', error);
      }
    );
  }
  addDashboard(name: string) {
    if (name.length != 0) {
      // Crear un nuevo dashboard con el nombre proporcionado y un array vacío para 'vista'
      const newDashboard: Dashboard = { nombre: name,count:[], map: [],vista: [] };

      // Verificar si 'userDashboard' y 'userDashboard.data' existen
      if (this.userDashboard && this.userDashboard.data) {
        // Verificar si ya existe un dashboard con el mismo nombre
        const existingDashboard = this.userDashboard.data.dashboards.find(dashboard => dashboard.nombre === name);

        if (!existingDashboard) {
          // Si no existe, añadir el nuevo dashboard al array 'dashboards'
          this.userDashboard.data.dashboards.push(newDashboard);

          // Aquí puedes guardar los cambios en el servicio o donde lo necesites
          this.userService.setUserDashboard(this.userDashboard);
          this.okDash.nativeElement.click()
        } else {
          this.errorDash.nativeElement.click()
        }
      }
    } else {
      this.validName.nativeElement.click()

    }
  }


  open(dashboard: Dashboard) {
    this.loadDashboard(true,  dashboard);
  }

  addHoverClass() {
    if (this.parentUl != undefined) {
      console.log("SIIII")
      this.parentUl.nativeElement.classList.add('active-list');
    }
  }

  removeHoverClass() {
    if (this.parentUl != undefined) {
      console.log("NOOO")

      this.parentUl.nativeElement.classList.remove('active-list');
    }
  }
}
