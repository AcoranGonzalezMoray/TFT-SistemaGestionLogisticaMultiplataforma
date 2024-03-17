import { Component, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { GridsterConfig, GridsterItem, GridsterItemComponent } from 'angular-gridster2';
import { GridItemServiceService } from '../services/grid-item-service.service';
import { VistaComponent } from '../vista/vista.component';
import { Count, Dashboard, MapDash, Widget } from '../interfaces/dashboard';
import { gridTypes } from 'angular-gridster2/lib/gridsterConfig.interface';
import { DataService } from '../services/data.service';
import { View } from '../interfaces/view';
import { animate, style, transition, trigger } from '@angular/animations';
import { HttpClient } from '@angular/common/http';
import { CountComponent } from '../count/count.component';
import { MapComponent } from '../map/map.component';
import { WidgetTaskListComponent } from '../widget-task-list/widget-task-list.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  animations: [
    trigger('fadeInOut', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('1000ms', style({ opacity: 1 })),
      ]),
      transition(':leave', [
        animate('1000ms', style({ opacity: 0 })),
      ]),
    ]),
  ],
})
export class DashboardComponent {
  options!: GridsterConfig;
  dashboard!: Array<GridsterItem>;
  dashboardData: Dashboard | undefined
  modeEdit: Boolean = false
  unitHeight: number = 0;


  constructor(private http: HttpClient, public gridItemService: GridItemServiceService, private userService: DataService) { }


  ngOnInit() {
    this.loadItemsConfigJson()
    this.options = {
      itemChangeCallback: DashboardComponent.itemChange,
      itemResizeCallback: (item, itemComponent) => this.itemResize(item, itemComponent),
      gridType: 'scrollVertical'

    };
    this.gridItemService.getDashboardObser().subscribe(dashboard => {
      if (dashboard) {
        this.loadItemsConfigJson()
        this.options = {
          itemChangeCallback: DashboardComponent.itemChange,
          itemResizeCallback: (item, itemComponent) => this.itemResize(item, itemComponent),
          gridType: 'scrollVertical'

        };
      }
    })
  }

  getGridsterItemsConfigJson(): { posicion: string }[] {
    const itemsConfig = this.dashboard
      .filter(item => item['chartType']) // Filtrar solo los elementos con chartType definido
      .map(item => ({ posicion: JSON.stringify({ cols: item.cols, rows: item.rows, x: item.x, y: item.y, chartType: item['chartType'] }) }));
  
    return itemsConfig;
  }
  
  getGridsterCountConfigJson(): { posicion: string, title:string }[] {
    const itemsConfig = this.dashboard
    .filter(item => item['title'])
    .map(item => ({
      posicion: JSON.stringify({ cols: item.cols, rows: item.rows, x: item.x, y: item.y}),
      title: item['title'],
    }));

    return itemsConfig;
  }
  getGridsterMapConfigJson(): { posicion: string, map:string }[] {
    const itemsConfig = this.dashboard
    .filter(item => item['map'])
    .map(item => ({
      posicion: JSON.stringify({ cols: item.cols, rows: item.rows, x: item.x, y: item.y}),
      map: item['map'],
    }));

    return itemsConfig;
  }
  getGridsterWidgetConfigJson(): { posicion: string, name:string, objectT:[] }[] {
    const itemsConfig = this.dashboard
    .filter(item => item['name'])
    .map(item => ({
      posicion: JSON.stringify({ cols: item.cols, rows: item.rows, x: item.x, y: item.y}),
      name: item['name'],
      objectT: item['objectT'],
    }));

    return itemsConfig;
  }

  static itemChange(item: any, itemComponent: any) {
    console.info('itemChanged', item, itemComponent);

  }

  itemResize(item: any, itemComponent: any) {
    console.info('itemResized', item, itemComponent);
    this.gridItemService.setSize([itemComponent.width, itemComponent.height, item])
  }


  changedOptions() {
    if (this.options && this.options.api && this.options.api.optionsChanged) {
      this.options.api.optionsChanged();
    }
  }

  loadItemsConfigJson() {
    this.dashboardData = this.gridItemService.getDashboard();
    console.log(this.dashboardData)
    const vistas = this.dashboardData?.vista;

    if (vistas) {
      this.dashboard = vistas.flatMap(vista => {
        const posicionString = vista.posicion;

        try {
          // Intentar parsear la cadena JSON
          const posicion = JSON.parse(posicionString);

          const itemConfig: GridsterItem = {
            cols: posicion.cols || 1,
            rows: posicion.rows || 1,
            x: posicion.x || 0,
            y: posicion.y || 0,
            chartType: posicion.chartType
          };

          itemConfig['componentType'] = VistaComponent;
          console.log(itemConfig['componentType'])
          return [itemConfig];
        } catch (error) {
          console.error('Error al parsear la propiedad "posicion":', error);
          return [];
        }
      });
    }

    this.changedOptions();
    this.loadCounts()
  }

  loadCounts(): void {
    this.dashboardData?.count?.forEach(count => {
      const posicionString = count.posicion;

      try {
        // Intentar parsear la cadena JSON
        const posicion = JSON.parse(posicionString);

        const itemConfig: GridsterItem = {
          cols: posicion.cols || 1,
          rows: posicion.rows || 1,
          x: posicion.x || 0,
          y: posicion.y || 0,
          componentTypeCount: CountComponent,
          title: count.title
        };

        this.dashboard.push(itemConfig);
      } catch (error) {
        console.error('Error al parsear la propiedad "posicion":', error);
      }
    });

    this.loadMaps()
  }
  loadMaps(): void {
    var local:any = []
    this.dashboardData?.map?.forEach(map => {
      const posicionString = map.posicion;

      try {
        // Intentar parsear la cadena JSON
        const posicion = JSON.parse(posicionString);

        const itemConfig: GridsterItem = {
          cols: posicion.cols || 1,
          rows: posicion.rows || 1,
          x: posicion.x || 0,
          y: posicion.y || 0,
          componentTypeMap: MapComponent,
          map: map.map
        };
        this.dashboard.push(itemConfig);
      } catch (error) {
        console.error('Error al parsear la propiedad "posicion":', error);
      }
    });
    this.loadWidget();
  }

  loadWidget(): void {
    var local:any = []
    this.dashboardData?.widget?.forEach(widget => {
      const posicionString = widget.posicion;

      try {
        // Intentar parsear la cadena JSON
        const posicion = JSON.parse(posicionString);

        const itemConfig: GridsterItem = {
          cols: posicion.cols || 1,
          rows: posicion.rows || 1,
          x: posicion.x || 0,
          y: posicion.y || 0,
          componentTypeWidget: WidgetTaskListComponent,
          name: widget.name,
          objectT: widget.objectT
        };
        this.dashboard.push(itemConfig);
      } catch (error) {
        console.error('Error al parsear la propiedad "posicion":', error);
      }
    });
  }

  saveItemsConfigJson() {
    // Obtener el usuario actual
    this.userService.getUserDashboard().subscribe((user) => {
      const jsonConfig = this.getGridsterItemsConfigJson();

      if (user.data?.dashboards && user.data.dashboards.length > 0) {
        const dashboardActual = user.data.dashboards.find((dashboard) => dashboard.nombre === this.dashboardData?.nombre);
        if (dashboardActual?.vista) {
          // Asegurar que haya suficientes elementos en vista
          const newVista: View[] = new Array(jsonConfig.length).fill("");
          console.log("CONMFIG"+jsonConfig)
          // Actualizar las posiciones
          newVista.forEach((vistaR, index) => {
            const vista: View = { posicion: jsonConfig[index].posicion}
            newVista[index] = vista
          });
          dashboardActual.vista = newVista
          this.userService.setUserDashboard(user);
          this.saveCountsConfigJson()
        }
      }
      console.log('Configuración del usuario actualizada con éxito');
    });
  }
  saveCountsConfigJson() {
    // Obtener el usuario actual
    this.userService.getUserDashboard().subscribe((user) => {
      const jsonConfig = this.getGridsterCountConfigJson(); // Obtener la configuración de los counts
  
      if (user.data?.dashboards && user.data.dashboards.length > 0) {
        const dashboardActual = user.data.dashboards.find((dashboard) => dashboard.nombre === this.dashboardData?.nombre);
        if (dashboardActual) {
          const newCount: Count[] = new Array(jsonConfig.length).fill("");
          newCount.forEach((vistaR, index) => {
            const count: Count = {title: jsonConfig[index].title, posicion: jsonConfig[index].posicion}
            newCount[index] = count
          });
          dashboardActual.count = newCount
          this.userService.setUserDashboard(user);
          this.saveMapConfigJson()
          console.log('Configuración de los counts del usuario actualizada con éxito');
        } else {
          console.error('No se encontró el dashboard en los datos del usuario');
        }
      } else {
        console.error('No se encontró el dashboard en los datos del usuario');
      }
    });
  }
  saveMapConfigJson() {
    // Obtener el usuario actual
    this.userService.getUserDashboard().subscribe((user) => {
      const jsonConfig = this.getGridsterMapConfigJson(); // Obtener la configuración de los counts
  
      if (user.data?.dashboards && user.data.dashboards.length > 0) {
        const dashboardActual = user.data.dashboards.find((dashboard) => dashboard.nombre === this.dashboardData?.nombre);
        if (dashboardActual) {
          const newCount: MapDash[] = new Array(jsonConfig.length).fill("");
          newCount.forEach((vistaR, index) => {
            const count: MapDash = {map: jsonConfig[index].map, posicion: jsonConfig[index].posicion}
            newCount[index] = count
          });
          dashboardActual.map = newCount
          this.userService.setUserDashboard(user);
          this.saveWidgetConfigJson()
          console.log('Configuración de los maps del usuario actualizada con éxito');
        } else {
          console.error('No se encontró el dashboard en los datos del usuario');
        }
      } else {
        console.error('No se encontró el dashboard en los datos del usuario');
      }
    });
  }
  saveWidgetConfigJson() {
    // Obtener el usuario actual
    this.userService.getUserDashboard().subscribe((user) => {
      const jsonConfig = this.getGridsterWidgetConfigJson(); // Obtener la configuración de los counts
      console.log("WIDGEET", JSON.stringify(jsonConfig));
      if (user.data?.dashboards && user.data.dashboards.length > 0) {
        const dashboardActual = user.data.dashboards.find((dashboard) => dashboard.nombre === this.dashboardData?.nombre);
        if (dashboardActual) {
          const newCount: Widget[] = new Array(jsonConfig.length).fill("");
          newCount.forEach((vistaR, index) => {
            const count: Widget = {name: jsonConfig[index].name, objectT: jsonConfig[index].objectT, posicion: jsonConfig[index].posicion}
            newCount[index] = count
          });
          dashboardActual.widget = newCount
          this.userService.setUserDashboard(user);

          console.log('Configuración de los WIDGET del usuario actualizada con éxito');
        } else {
          console.error('No se encontró el dashboard en los datos del usuario');
        }
      } else {
        console.error('No se encontró el dashboard en los datos del usuario');
      }
    });
  }

  addItem(cols: number, rows: number, chartType: string) {
    if (chartType.includes("Count:")) this.addItemCount(cols, rows, chartType);
    else if(chartType.includes("Map:"))  this.addItemMap(cols, rows, chartType);
    else if(chartType.includes("Widget:"))  this.addItemWidget(cols, rows, chartType);
    else if (chartType.includes("Chart:")) {
      const newItem: GridsterItem = {
        x: 0,
        y: 0,
        cols: cols,
        rows: rows,
        componentType: VistaComponent,
        chartType: chartType,
        height: 400, // ajusta según tus necesidades
        width: 600, // ajusta según tus necesidades
      };
      this.dashboard.push(newItem);
    }
  }

  addItemCount(cols: number, rows: number, titulo: string) {
    const newItem = {
      x: 0,
      y: 0,
      cols: cols,
      rows: rows,
      componentTypeCount: CountComponent,
      title: titulo,
      height: 100, // ajusta según tus necesidades
      width: 100, // ajusta según tus necesidades
    };
    this.dashboard.push(newItem);
  }

  addItemMap(cols: number, rows: number, titulo: string) {
    const newItem = {
      x: 0,
      y: 0,
      cols: cols,
      rows: rows,
      componentTypeMap: MapComponent,
      map: titulo,
      height: 300, // ajusta según tus necesidades
      width: 300, // ajusta según tus necesidades
    };
    console.log("MAAAAP")
    this.dashboard.push(newItem);
  }
  addItemWidget(cols: number, rows: number, titulo: string) {
    const newItem = {
      x: 0,
      y: 0,
      cols: cols,
      rows: rows,
      componentTypeWidget: WidgetTaskListComponent,
      name: titulo,
      objectT: [],
      height: 300, // ajusta según tus necesidades
      width: 300, // ajusta según tus necesidades
    };
    console.log("MAAAAP")
    this.dashboard.push(newItem);
  }
  removeItem(item: any) {
    this.dashboard.splice(this.dashboard.indexOf(item), 1);
  }

  changeGridType(gridType: gridTypes | undefined) {
    this.options.gridType = gridType;
    this.changedOptions();
  }

  modoEdit(mode: boolean) {
    this.modeEdit = mode;
    if (mode) {
      this.changeGridType("fixed")
      this.options = {
        itemChangeCallback: DashboardComponent.itemChange,
        itemResizeCallback: (item, itemComponent) => this.itemResize(item, itemComponent),
        displayGrid: 'always', // Opciones: 'always' | 'onDrag&Resize' | 'none'
        draggable: {
          enabled: true,
        },
        resizable: {
          enabled: true,
        },
      };
    } else {
      this.changeGridType("scrollVertical")
      this.options = {
        itemChangeCallback: DashboardComponent.itemChange,
        itemResizeCallback: (item, itemComponent) => this.itemResize(item, itemComponent),
        displayGrid: 'none', // Opciones: 'always' | 'onDrag&Resize' | 'none'
        draggable: {
          enabled: false,
        },
        resizable: {
          enabled: false,
        },
      };
    }
    this.options.displayGrid = mode ? 'always' : 'none';
    this.changedOptions(); // Asegúrate de notificar a la cuadrícula sobre los cambios
  }
}
