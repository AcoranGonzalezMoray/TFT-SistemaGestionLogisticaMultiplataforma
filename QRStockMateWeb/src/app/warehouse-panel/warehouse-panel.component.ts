import { Component, ElementRef, ViewChild } from '@angular/core';
import { Warehouse } from '../interfaces/warehouse';
import { MatTableDataSource } from '@angular/material/table';
import { User } from '../interfaces/user';
import { MatPaginator } from '@angular/material/paginator';
import { CompanyService } from '../services/company.service';
import { UserService } from '../services/user.service';
import { Company } from '../interfaces/company';
import { rowsAnimation } from 'src/assets/animations';
import { GetResourceResponse, LngLatLike, Map, Marker } from 'maplibre-gl';

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



  map: Map | undefined;

  @ViewChild('map') mapContainer! : ElementRef<HTMLElement>;

  initMap(){
    const mapContainer = document.getElementById('containerMap');
    if (mapContainer) {
      mapContainer.style.display = 'none';
    }

    const initialState = { lng: 139.753, lat: 35.6844, zoom: 14 };

    this.map = new Map({
      container: this.mapContainer.nativeElement,
      style: `https://api.maptiler.com/maps/streets-v2/style.json?key=UJ8HxUte4EhYzt4gJPBK`,
      center: [initialState.lng, initialState.lat],
      zoom: initialState.zoom
    });

  }


  ngAfterViewInit(): void {
    //Called after ngAfterContentInit when the component's view has been initialized. Applies to components only.
    //Add 'implements AfterViewInit' to the class.
    this.initMap()
  }


  openMap(lat: number, lon: number, name: string): void {
    // Mostrar el mapa
    const mapContainer = document.getElementById('containerMap');
    const table = document.getElementById('matelevationz8');

    if (mapContainer && table) {
      table.style.display = 'none'
      mapContainer.style.display = 'block';
    }

    if (this.map) {
        const existingMarkers = this.map.getLayer('marker-layer');
        if (existingMarkers) {
            this.map.removeLayer('marker-layer');
            this.map.removeSource('marker-layer');
        }

        const imageUrl = '../../assets/images/warehouse.png'; // URL de tu imagen personalizada
        const markerLocation: LngLatLike = [lon, lat]; // Ubicación del marcador

        // Cargar la imagen
        this.map.loadImage(imageUrl).then((response: GetResourceResponse<HTMLImageElement | ImageBitmap>) => {
            // Obtener la imagen cargada
            const image = response.data;

            // Agregar marcador utilizando la imagen cargada
            this.map!.addImage('custom-marker', image);

            this.map!.addLayer({
                id: 'marker-layer',
                type: 'symbol',
                source: {
                    type: 'geojson',
                    data: {
                        type: 'FeatureCollection',
                        features: [{
                            type: 'Feature',
                            geometry: {
                                type: 'Point',
                                coordinates: markerLocation
                            },
                            properties: {
                              name: name
                            } // Propiedades vacías o las que desees asociar con el marcador
                        }]
                    }
                },
                layout: {
                    'icon-image': 'custom-marker',
                    'icon-size': 0.1, // Reducir el tamaño del marcador a la mitad (0.4 -> 0.2)
                    'text-field': ['get', 'name'], // Mostrar el nombre del marcador
                    'text-font': ['Open Sans Regular'],
                    'text-offset': [0, 1.5],
                    'text-anchor': 'top'
                }
            });

            // Centrar el mapa en la ubicación del marcador
            this.map!.setCenter(markerLocation);
        }).catch((error: any) => {
            console.error('Error al cargar la imagen:', error);
        });
    }
  }





  closeMap(): void {
    // Ocultar el mapa
    const mapContainer = document.getElementById('containerMap');
    const table = document.getElementById('matelevationz8');

    if (mapContainer && table) {
      table.style.display = 'block'
      mapContainer.style.display = 'none';
    }
  }
  
  ngOnDestroy() {
    this.map?.remove();
  }
}
