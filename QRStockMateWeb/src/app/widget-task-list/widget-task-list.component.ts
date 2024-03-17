import { Component, Input, SimpleChanges } from '@angular/core';
import { Dashboard, Task } from '../interfaces/dashboard';
import { GridItemServiceService } from '../services/grid-item-service.service';
import { ItemService } from '../services/item.service';
import { GridsterItem } from 'angular-gridster2';
import { DataService } from '../services/data.service';

@Component({
  selector: 'app-widget-task-list',
  templateUrl: './widget-task-list.component.html',
  styleUrls: ['./widget-task-list.component.css']
})
export class WidgetTaskListComponent {
  tasks: Task[] = []
  newTask: string = '';
  width: number = 500;
  height: number = 500
  @Input() item!: GridsterItem;
  @Input() objectT!: Task[];
  dashboardData: Dashboard | undefined

  constructor(private itemService: ItemService, private gridItemService: GridItemServiceService, private userService: DataService) { }

  ngOnInit(): void {
    this.tasks = this.objectT
  }


  ngOnChanges(changes: SimpleChanges): void {
    if (changes['item']) {
      console.log('Nuevo tamaño del elemento:', this.width, 'x', this.height);
      this.handleItemChange();
    }
  }

  private handleItemChange(): void {


    this.gridItemService.getSize().subscribe(size => {
      if (size[2] == this.item) {

        this.width = size[0] // Ajustar el ancho según el número de columnas
        this.height = size[1] // Ajustar la altura según el número de filas

        // Imprimir para verificar el cambio en las dimensiones
        console.log('Nuevo tamaño del elemento:', this.width, 'x', size);
      }
    })
  }


  addTask(): void {
    this.dashboardData = this.gridItemService.getDashboard();

    this.userService.getUserDashboard().subscribe((user) => {
      if (user.data?.dashboards && user.data.dashboards.length > 0) {
        const dashboardActual = user.data.dashboards.find((dashboard) => dashboard.nombre === this.dashboardData?.nombre);
        if (dashboardActual) {
          dashboardActual.widget.forEach((element) => {
            if (element.name == this.item['name'] &&
              JSON.stringify(element.objectT) == JSON.stringify(this.item['objectT'])) {
              if (this.newTask.trim() !== '') {
                this.tasks.push({ name: this.newTask, completed: false });
                this.newTask = '';
                element.objectT = this.tasks
                this.userService.setUserDashboard(user)

              }
            }
          })

          console.log('Configuración de los WIDGET del usuario actualizada con éxito');
        } else {
          console.error('No se encontró el dashboard en los datos del usuario');
        }
      } else {
        console.error('No se encontró el dashboard en los datos del usuario');
      }
    });
  }
  oldTask:Task[] = [];
  toggleTaskCompletion(index: number): void {
    this.oldTask = this.tasks.map(task => ({ ...task }));
    this.tasks[index].completed = !this.tasks[index].completed;//true
    console.log(this.oldTask)
    console.log(this.tasks)

    this.dashboardData = this.gridItemService.getDashboard();

    // Buscar el dashboard actual en los datos del usuario
    this.userService.getUserDashboard().subscribe((user) => {
      if (user.data?.dashboards && user.data.dashboards.length > 0) {
        const dashboardActual = user.data.dashboards.find((dashboard) => dashboard.nombre === this.dashboardData?.nombre);
        if (dashboardActual) {
          // Encontrar el elemento en el dashboard actual que corresponde al widget actual
          const widgetElement = dashboardActual.widget.find((element) =>
            element.name == this.item['name'] &&
            JSON.stringify(element.objectT) == JSON.stringify( this.oldTask )
          );
          console.log(JSON.stringify( this.oldTask ))
          if (widgetElement) {
            // Actualizar el objetoT en el widgetElement
            widgetElement.objectT = this.tasks;

            // Actualizar el dashboard en el servicio de usuario
            this.userService.setUserDashboard(user)
          } else {
            console.error('No se encontró el widget en el dashboard del usuario');
          }
        } else {
          console.error('No se encontró el dashboard en los datos del usuario');
        }
      } else {
        console.error('No se encontró el dashboard en los datos del usuario');
      }
    });
  }
}
