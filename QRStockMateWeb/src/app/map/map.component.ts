import { Component, ElementRef, ViewChild } from '@angular/core';
import { GetResourceResponse, LngLatLike, Map, Marker } from 'maplibre-gl';
import { Company } from '../interfaces/company';
import { User } from '../interfaces/user';
import { Warehouse } from '../interfaces/warehouse';
import { CompanyService } from '../services/company.service';
import { UserService } from '../services/user.service';
import { key } from '../../environment/keys';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent {
  isLoading: Boolean = false
  company!: Company;
  warehouses: Warehouse[] = [];
  token: string = ""

  constructor(private companyService: CompanyService, private userService: UserService) { }

  getCompanyByUser(): void {
    var stringT = sessionStorage.getItem('token')
    var stringU = sessionStorage.getItem('me')
    var user: User;
    if (stringT && stringU) {
      this.token = stringT;
      user = JSON.parse(stringU);
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


  loadWarehouse(): void {
    this.companyService.getWarehouses(this.company, this.token)
      .subscribe(warehousesNew => {
        this.warehouses = warehousesNew
        this.initMap()   
      }, error => {

      });
  }

  map: Map | undefined;

  @ViewChild('map') mapContainer!: ElementRef<HTMLElement>;

  initMap() {
    const initialState = { lng: 28.116924, lat: -15.449493, zoom: 14 };

    this.map = new Map({
      container: this.mapContainer.nativeElement,
      style: `https://api.maptiler.com/maps/streets-v2/style.json?key=`+key.MAP,
      center: [initialState.lng, initialState.lat],
      zoom: initialState.zoom
    });
    this.openMap()
  }


  ngAfterViewInit(): void {
    //Called after ngAfterContentInit when the component's view has been initialized. Applies to components only.
    //Add 'implements AfterViewInit' to the class.
 
    this.isLoading = true
    this.getCompanyByUser()
  }


  openMap(): void {
    this.warehouses?.forEach((element: Warehouse) => {
          // lat: number, lon: number, name: string
      var lat = element.latitude
      var lon = element.longitude
      var name = element.name

      if (this.map) {
        const existingMarkers = this.map.getLayer('marker-layer-'+element.name);
        if (existingMarkers) {
          this.map.removeLayer('marker-layer-'+element.name);
          this.map.removeSource('marker-layer-'+element.name);
        }

        const imageUrl = '../../assets/images/warehouse.png'; // URL de tu imagen personalizada
        const markerLocation: LngLatLike = [lon, lat]; // Ubicación del marcador

        // Cargar la imagen
        this.map.loadImage(imageUrl).then((response: GetResourceResponse<HTMLImageElement | ImageBitmap>) => {
          // Obtener la imagen cargada
          const image = response.data;

          // Agregar marcador utilizando la imagen cargada
          this.map!.addImage('custom-marker-'+element.name, image);

          this.map!.addLayer({
            id: 'marker-layer-'+element.name,
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
              'icon-image': 'custom-marker-'+element.name,
              'icon-size': 0.1, // Reducir el tamaño del marcador a la mitad (0.4 -> 0.2)
              'text-field': ['get', 'name'], // Mostrar el nombre del marcador
              'text-font': ['Open Sans Regular'],
              'text-offset': [0, 1.5],
              'text-anchor': 'top'
            }
          });

          // Centrar el mapa en la ubicación del marcador
          this.map!.setCenter({lat:27.9633405, lon:-15.5920098});
          this.map!.setZoom(10);

        }).catch((error: any) => {
          console.error('Error al cargar la imagen:', error);
        });
      }
    })

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
