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
import * as ExcelJS from 'exceljs';
import { baseImage } from 'src/assets/imagebase64';
import { WarehouseService } from '../services/warehouse.service';
@Component({
  selector: 'app-warehouse-panel',
  templateUrl: './warehouse-panel.component.html',
  styleUrls: ['./warehouse-panel.component.css'],
  animations: [rowsAnimation],
})

export class WarehousePanelComponent {

  displayedColumns: string[] = ['id', 'name', 'administrator', 'location', 'organization', 'numº Item', 'action'];
  dataSource = new MatTableDataSource<Warehouse>();
  token: string = ""
  warehouse: Warehouse | undefined;
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;
  clickedRows = new Set<Warehouse>();
  @ViewChild('notify') noty!: ElementRef;
  @ViewChild('notifEmpty') notE!: ElementRef;
  @ViewChild('closeModal') closeModal!: ElementRef;

  isLoading: Boolean = false
  warehouses: Warehouse[] = [];
  users: User[] = [];
  company!: Company;
  @ViewChild('selectw') selectw!: ElementRef;

  constructor(private warehouseService: WarehouseService, private companyService: CompanyService, private userService: UserService) { }

  ngOnInit(): void {
    this.isLoading = true
    this.getCompanyByUser()
  }


  setWarehouse(warehouse: Warehouse) {
    this.warehouse = { ...warehouse };
    this.selectw.nativeElement.value = this.warehouse?.idAdministrator;
  }

  updateWarehouseAd(selectedUserId: any) {
    this.warehouse!.idAdministrator = Number(selectedUserId.target.value);
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
          this.loadWarehouse();
          this.loadEmployees();
        }, 1000);

      }, error => {
        console.error('Error getting company by user:', error.message); // Aquí se imprime solo el mensaje de error
      });
  }

  searchByValue(element: HTMLInputElement) {
    this.dataSource.filter = element.value.trim().toLowerCase();

  }

  loadEmployees(): void {
    this.companyService.getEmployees(this.company, this.token)
      .subscribe(employees => {
        this.users = employees.filter(u => u.role == 1);
      });
  }

  loadWarehouse(): void {
    this.companyService.getWarehouses(this.company, this.token)
      .subscribe(warehousesNew => {
        const warehouses: Warehouse[] = [];
        this.warehouses = warehousesNew

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


  updateWarehouse(name: string, location: string, organization: string) {
    this.isLoading = true

    var war = this.warehouse!
    war.name = name
    war.location = location
    war.organization = organization


    this.warehouseService.updateWarehouse(war, this.token)
      .subscribe(z => {
        setTimeout(() => {
          this.getCompanyByUser()
          this.noty.nativeElement.click()
          this.closeModal.nativeElement.click()
        }, 2500)
      })
  }


  map: Map | undefined;

  @ViewChild('map') mapContainer!: ElementRef<HTMLElement>;

  initMap() {
    const mapContainer = document.getElementById('containerMap');
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



  async export() {
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('Warehouses');

    // Definir los datos
    const headerRow = ['ID', 'Name', 'ID Administrator', 'Location', 'Organization', 'ID Items'];

    // Agregar el logo
    worksheet.addRow([]);
    worksheet.addRow([]);
    worksheet.addRow([]);
    worksheet.addRow([]);
    worksheet.addRow(headerRow);

    worksheet.mergeCells('A1:F4');
    // Obtener la celda fusionada
    const mergedCell = worksheet.getCell('A1');
    mergedCell.value = 'QRSTOCKMATE'
    mergedCell.fill = {
      type: 'pattern',
      pattern: 'solid',
      fgColor: { argb: 'FF5a79ba' } // Color #222222
    };

    // Agregar los datos de los almacenes
    this.warehouses.forEach((warehouse: Warehouse) => {
      worksheet.addRow([
        warehouse.id.toString(), // ID
        warehouse.name,         // Nombre
        warehouse.idAdministrator.toString(), // ID del administrador
        warehouse.location,    // Ubicación
        warehouse.organization, // Organización
        warehouse.idItems      // Número de ítem
      ]);
    });

    // Establecer el tamaño de fuente y centrar el contenido de las celdas
    worksheet.eachRow({ includeEmpty: true }, (row, rowNumber) => {
      row.eachCell({ includeEmpty: true }, (cell, colNumber) => {
        cell.font = { size: 13 };
        cell.border = {
          top: { style: 'thin' },
          left: { style: 'thin' },
          bottom: { style: 'thin' },
          right: { style: 'thin' }
        };
        cell.alignment = { horizontal: 'center', vertical: 'middle' };
      });
    });
    worksheet.getRow(4).eachCell({ includeEmpty: false }, (cell, colNumber) => {
      cell.font = { bold: true }
    });
    worksheet.getRow(1).eachCell({ includeEmpty: false }, (cell, colNumber) => {
      cell.font = { bold: true, color: { argb: 'FFFFFFFF' } }; // Color blanco en hexadecimal
    });

    // Agregar imagen base64 como logo
    const base64Image = 'data:image/png;base64,' + baseImage; // Reemplaza baseImage con tu imagen base64
    const imageId = workbook.addImage({
      base64: base64Image,
      extension: 'png',
    });

    worksheet.addImage(imageId, "A1:A4");

    // Ajustar el ancho de las columnas según el tamaño del logo
    worksheet.columns.forEach((column, index) => {
      if (index === 3) { // Verifica si es la columna D (0-indexed)
        column.width = 90; // Ajusta el ancho de la columna D
      } else {
        column.width = 20; // Ajusta el ancho de las demás columnas
      }
    });

    // Guardar el archivo
    const buffer = await workbook.xlsx.writeBuffer();
    const blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    const fileName = 'QRSTOCKMATE_Warehouse_Report_' + new Date().toISOString() + '.xlsx';
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = fileName;
    link.click();
  }


  returnNumItems(element: Warehouse) {
    var num = element.idItems.split(';').length

    return num - 1
  }
}
