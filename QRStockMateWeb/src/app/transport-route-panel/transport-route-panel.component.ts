import { Component, ElementRef, ViewChild } from '@angular/core';
import { RoleStatus, TransportRoute, getRoleStatus } from '../interfaces/transport-route';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { Company } from '../interfaces/company';
import { CompanyService } from '../services/company.service';
import { UserService } from '../services/user.service';
import { User } from '../interfaces/user';
import { GetResourceResponse, LngLatLike, Map } from 'maplibre-gl';
import { TransportRouteService } from '../services/transport-route.service';
import { rowsAnimation } from 'src/assets/animations';
import { Warehouse } from '../interfaces/warehouse';
import { VehicleService } from '../services/vehicle.service';

@Component({
  selector: 'app-transport-route-panel',
  templateUrl: './transport-route-panel.component.html',
  styleUrls: ['./transport-route-panel.component.css'],
  animations: [rowsAnimation]

})
export class TransportRoutePanelComponent {
  displayedColumns: string[] = ['id', 'code', 'startLocation', 'endLocation', 'departureTime', 'arrivalTime', 'assignedVehicleId', 'carrierId', 'status', 'action'];
  dataSource = new MatTableDataSource<TransportRoute>();
  token: string = ""
  transportRoute: TransportRoute | undefined;
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;
  clickedRows = new Set<TransportRoute>();
  @ViewChild('notifyT') noty!: ElementRef;
  @ViewChild('notifEmptyT') notE!: ElementRef;
  isLoading: Boolean = false
  open: Boolean = false
  company!: Company;
  warehouses: Warehouse[] | undefined;
  palets: Palet[] = [];
  totalWeight: number = 0;

  constructor(private vehicleService: VehicleService, private companyService: CompanyService, private routeService: TransportRouteService, private userService: UserService) { }

  ngOnInit(): void {
    this.isLoading = true

    this.getCompanyByUser()
  }

  parsePalets(paletsString: string): void {
    const [paletsArray, total] = this.parsePaletsString(paletsString);
    this.palets = paletsArray;
    this.totalWeight = total;
  }

  parsePaletsString(paletsString: string): [Palet[], number] {
    const palets: Palet[] = [];
    let totalWeight = 0;

    const trimmedListString = paletsString.trim().slice(1, -1);
    const trimmedStringComplete = trimmedListString.split("},");

    for (const item of trimmedStringComplete) {
      let trimmedString = item.replace("{", "").replace("}", "").trim(); // 46=46:2:201.3;49=49:1:1.0; | 46=46:2:201.3;
      const mapStrings = trimmedString.split(";");

      for (const mapStr of mapStrings) {
        const [key, value] = mapStr.split("=");

        if (key.trim() !== "" && value) {
          const weight = parseFloat(value.split(":")[2]);
          if (!isNaN(weight)) {
            totalWeight += weight;
          }

          const palet: Palet = {
            id: parseInt(key.trim()),
            description: value.trim() + ";",
            weight: weight
          };

          palets.push(palet);
        }
      }
    }

    return [palets, totalWeight];
  }





  setRoute(TransportRoute: TransportRoute) {
    this.transportRoute = TransportRoute
  }

  getLocation(point: string) {
    return this.warehouses?.filter(x => x.id == parseInt(point))[0]!.name
  }

  getStatus(status: string) {
    return getRoleStatus(parseInt(status));
  }
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
          this.loadTransportRoute();
        }, 1000);

      }, error => {
        console.error('Error getting company by user:', error.message); // Aquí se imprime solo el mensaje de error
      });
  }
  searchByValue(element: HTMLInputElement) {
    this.dataSource.filter = element.value.trim().toLowerCase();

  }

  loadTransportRoute(): void {
    this.routeService.getTransportRoutesByCode(this.company.code, this.token)
      .subscribe(routesNew => {
        if (routesNew.length == 0) {
          setTimeout(() => {
            this.isLoading = false
            this.notE.nativeElement.click()
          }, 1000);
        } else {
          const route: TransportRoute[] = [];

          routesNew.forEach((r, index) => {
            setTimeout(() => {
              route.push(r);
              this.dataSource.data = route;
            }, (index + 1) * 500);
          });


          this.dataSource.paginator = this.paginator;
          this.loadWarehouse()

        }


      }, error => {
        this.notE.nativeElement.click()
      });
  }



  map: Map | undefined;

  @ViewChild('map') mapContainer!: ElementRef<HTMLElement>;

  initMap() {
    const mapContainer = document.getElementById('containerMapRoute');
    if (mapContainer) {
      mapContainer.style.display = 'none';
    }

    const initialState = { lng: 28.116924, lat: -15.449493, zoom: 14 };

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

  loadWarehouse(): void {
    this.companyService.getWarehouses(this.company, this.token)
      .subscribe(warehousesNew => {
        this.warehouses = warehousesNew

      })
  }

  openMap(start: string, nameA: string, end: string, nameB: string, route: string, vehicleID: string, status: number, palets: string): void {
    this.parsePalets(palets);

    this.open = true
    //latA: number, lonA: number,  latB: number, lonB: number
    var START: Warehouse = this.warehouses?.filter(x => x.id == parseInt(start))[0]!
    var END = this.warehouses?.filter(x => x.id == parseInt(end))[0]!
    // Mostrar el mapa
    const mapContainer = document.getElementById('containerMapRoute');
    const table = document.getElementById('matelevationz8');

    if (mapContainer && table) {
      table.style.display = 'none'
      mapContainer.style.display = 'block';
    }

    if (this.map) {
      const existingMarkersA = this.map.getLayer('marker-layer-A');
      const existingMarkersB = this.map.getLayer('marker-layer-B');
      const existingPolyline = this.map.getLayer('polyline-layer');
      if (existingPolyline) {
        this.map.removeLayer('polyline-layer');
        this.map.removeSource('polyline-layer');
      }
      if (existingMarkersA && existingMarkersB) {
        this.map.removeLayer('marker-layer-A');
        this.map.removeSource('marker-layer-A');
        this.map.removeLayer('marker-layer-B');
        this.map.removeSource('marker-layer-B');
      }

      const imageUrl = '../../assets/images/warehouse.png'; // URL de tu imagen personalizada
      const markerLocationA: LngLatLike = [START.longitude, START.latitude]; // Ubicación del marcador
      const markerLocationB: LngLatLike = [END.longitude, END.latitude]; // Ubicación del marcador

      // Cargar la imagen
      this.map.loadImage(imageUrl).then((response: GetResourceResponse<HTMLImageElement | ImageBitmap>) => {
        if (route) {
          // Parsear la cadena de puntos en un array de strings
          const pointsArray = route.match(/lat\/lng: \(\s*-?\d+\.\d+,\s*-?\d+\.\d+\)/g);

          if (pointsArray) {
            // Convertir cada punto en el array de strings en un array de coordenadas [lat, lng]
            const coordinates: [number, number][] = pointsArray.map((point: string) => {
              // Extraer la latitud y longitud de cada punto
              const [lat, lng] = point.match(/-?\d+\.\d+/g)!.map(parseFloat);
              return [lat, lng];
            });
            console.log(coordinates)
            // Llamar a la función drawPolyline con las coordenadas convertidas
            this.drawPolyline(coordinates);
          } else {
            console.error('No se encontraron puntos en la cadena proporcionada.');
          }
        }
        // Obtener la imagen cargada
        const image = response.data;

        // Agregar marcador utilizando la imagen cargada
        this.map!.addImage('custom-marker', image);

        this.map!.addLayer({
          id: 'marker-layer-A',
          type: 'symbol',
          source: {
            type: 'geojson',
            data: {
              type: 'FeatureCollection',
              features: [{
                type: 'Feature',
                geometry: {
                  type: 'Point',
                  coordinates: markerLocationA
                },
                properties: {
                  name: nameA
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
        this.map!.addLayer({
          id: 'marker-layer-B',
          type: 'symbol',
          source: {
            type: 'geojson',
            data: {
              type: 'FeatureCollection',
              features: [{
                type: 'Feature',
                geometry: {
                  type: 'Point',
                  coordinates: markerLocationB
                },
                properties: {
                  name: nameB
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
        this.map!.setCenter(markerLocationA);
      }).catch((error: any) => {
        console.error('Error al cargar la imagen:', error);
      });


      if (status == 1) {
        this.mapInRoute(parseInt(vehicleID))
      }
    }
  }


  mapInRoute(vehicleID: number) {
    if (this.map) {
      const imageUrl = '../../assets/images/carrierbl.png'; // URL de tu imagen personalizada
      var index = 0
      this.map.loadImage(imageUrl).then((response: GetResourceResponse<HTMLImageElement | ImageBitmap>) => {
        const data = response.data
        this.map!.addImage('custom-marker-carrier', data);

        const updateLocation = () => {
          if (this.map) {
            this.vehicleService.getLocation(vehicleID, this.token).subscribe(vehicle => {
              if (this.map) {
                var location = vehicle.location.split(";");
                const markerLocation: LngLatLike = [parseFloat(location[1]), parseFloat(location[0])];
                console.log(vehicle);
                if (index == 0) {
                  this.map!.setCenter(markerLocation);
                  index++
                }

                // Agregar nueva capa
                if (this.map.getLayer('custom-layer-carrier1')) {
                  this.map!.addLayer({
                    id: 'custom-layer-carrier',
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
                            name: "Carrier"
                          }
                        }]
                      }
                    }, layout: {
                      'icon-image': 'custom-marker-carrier',
                      'icon-size': 0.1, // Reducir el tamaño del marcador a la mitad (0.4 -> 0.2)
                      'text-field': ['get', 'name'], // Mostrar el nombre del marcador
                      'text-font': ['Open Sans Regular'],
                      'text-offset': [0, 1.5],
                      'text-anchor': 'top'
                    }
                  });
                  if (this.map.getLayer('custom-layer-carrier1')) {
                    this.map.removeLayer('custom-layer-carrier1')
                    this.map.removeSource('custom-layer-carrier1');
                  }

                } else {

                  this.map!.addLayer({
                    id: 'custom-layer-carrier1',
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
                            name: "Carrier"
                          }
                        }]
                      }
                    }, layout: {
                      'icon-image': 'custom-marker-carrier',
                      'icon-size': 0.1, // Reducir el tamaño del marcador a la mitad (0.4 -> 0.2)
                      'text-field': ['get', 'name'], // Mostrar el nombre del marcador
                      'text-font': ['Open Sans Regular'],
                      'text-offset': [0, 1.5],
                      'text-anchor': 'top'
                    }
                  });


                  if (this.map.getLayer('custom-layer-carrier')) {
                    this.map.removeLayer('custom-layer-carrier');
                    this.map.removeSource('custom-layer-carrier');
                  }
                }


              }


            });
          }
          if (this.open) setTimeout(updateLocation, 4000);
        };
        updateLocation();
      }).catch((error: any) => {
        console.error('Error al cargar la imagen:', error);
      });
    }

  }



  drawPolyline(coordinates: [number, number][]): void {
    if (this.map) {
      // Eliminar capa y fuente existentes si ya están presentes
      const existingPolyline = this.map.getLayer('polyline-layer');
      if (existingPolyline) {
        this.map.removeLayer('polyline-layer');
        this.map.removeSource('polyline-layer');
      }

      // Convertir las coordenadas en el formato adecuado para GeoJSON
      const geoJSONCoordinates = coordinates.map(coord => [coord[1], coord[0]]);

      // Crear una nueva fuente de datos para la polylinea
      this.map.addSource('polyline-layer', {
        type: 'geojson',
        data: {
          type: 'Feature',
          properties: {},
          geometry: {
            type: 'LineString',
            coordinates: geoJSONCoordinates
          }
        }
      });

      // Añadir la capa de polylinea al mapa
      this.map.addLayer({
        id: 'polyline-layer',
        type: 'line',
        source: 'polyline-layer',
        layout: {
          'line-join': 'round',
          'line-cap': 'round'
        },
        paint: {
          'line-color': '#222222', // Color de la polylinea
          'line-width': 5 // Ancho de la polylinea en píxeles
        }
      });
    }
  }





  closeMap(): void {
    // Ocultar el mapa
    const mapContainer = document.getElementById('containerMapRoute');
    const table = document.getElementById('matelevationz8');

    if (mapContainer && table) {
      table.style.display = 'block'
      mapContainer.style.display = 'none';
    }

    this.open = false
  }

  ngOnDestroy() {
    this.map?.remove();
  }
}


interface Palet {
  id: number;
  description: string;
  weight: number;
}