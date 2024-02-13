import { Component, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import { GridsterConfig, GridsterItem, GridsterItemComponent } from 'angular-gridster2';
import { GridItemServiceService } from '../services/grid-item-service.service';
import { VistaComponent } from '../vista/vista.component';
import { Dashboard } from '../interfaces/dashboard';
import { gridTypes } from 'angular-gridster2/lib/gridsterConfig.interface';
import { UserService } from '../services/user.service';
import { View } from '../interfaces/view';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent{
  options!: GridsterConfig;
  dashboard!: Array<GridsterItem>;
  dashboardData:Dashboard|undefined
  modeEdit:Boolean = false
  unitHeight: number = 0;
  

  constructor(public gridItemService: GridItemServiceService, private userService:UserService) {}
  

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

  getGridsterItemsConfigJson(): { cols: number; rows: number; x: number; y: number; chartType: string }[] {
    const itemsConfig = this.dashboard.map(item => ({
      cols: item.cols,
      rows: item.rows,
      x: item.x,
      y: item.y,
      chartType: item['chartType'], 
    }));

    return itemsConfig;
  }

  static itemChange(item: any, itemComponent: any) {
    console.info('itemChanged', item, itemComponent);
    
  }

  itemResize(item: any, itemComponent: any ) {
    console.info('itemResized', item, itemComponent);
    this.gridItemService.setSize([itemComponent.width,itemComponent.height, item])
  }


  changedOptions() {
    if (this.options && this.options.api && this.options.api.optionsChanged) {
      this.options.api.optionsChanged();
    }
  }

  loadItemsConfigJson() {
    this.dashboardData = this.gridItemService.getDashboard();
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
            chartType:posicion.chartType
          };
  
          itemConfig['componentType'] = itemConfig['componentType'] || VistaComponent;
          return [itemConfig];
        } catch (error) {
          console.error('Error al parsear la propiedad "posicion":', error);
          return [];
        }
      });
    }
  
    this.changedOptions();
  }
  
  
  saveItemsConfigJson() {
    // Obtener el usuario actual
    this.userService.getUserDashboard().subscribe((user) => {
      const jsonConfig = this.getGridsterItemsConfigJson();
  
      if (user.data?.dashboards && user.data.dashboards.length > 0) {
        const dashboardActual = user.data.dashboards.find((dashboard) => dashboard.nombre === this.dashboardData?.nombre);
        if (dashboardActual?.vista) {
          // Asegurar que haya suficientes elementos en vista
          const newVista:View[] = new Array(jsonConfig.length).fill("");
          console.log(newVista)
          // Actualizar las posiciones
          newVista.forEach((vistaR, index) => {
            const vista:View = {posicion: JSON.stringify(jsonConfig[index])}
            newVista[index] = vista
          });
          console.log(newVista)
          console.log(user.data.dashboards[0].vista )
          dashboardActual.vista = newVista
          this.userService.setUserDashboard(user);
        }
      }
  
      // Llamar a setUserDashboard para guardar la nueva configuración
      
      console.log('Configuración del usuario actualizada con éxito');
    });
  }

  
  addItem(cols: number, rows: number, chartType:string) {
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

  
  removeItem(item: any) {
    this.dashboard.splice(this.dashboard.indexOf(item), 1);
  }

  changeGridType(gridType: gridTypes | undefined) {
    this.options.gridType = gridType;
    this.changedOptions();
  }
  
  modoEdit(mode: boolean) {
    this.modeEdit = mode;
    if(mode){
      this.changeGridType("fixed")
      this.options = {
        itemChangeCallback: DashboardComponent.itemChange,
        itemResizeCallback:(item, itemComponent) => this.itemResize(item, itemComponent),
        displayGrid: 'always', // Opciones: 'always' | 'onDrag&Resize' | 'none'
        draggable: {
          enabled: true,
        },
        resizable: {
          enabled: true,
        },
      };
    }else {
      this.changeGridType("scrollVertical")
      this.options = {
        itemChangeCallback: DashboardComponent.itemChange,
        itemResizeCallback:(item, itemComponent) => this.itemResize(item, itemComponent),
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
