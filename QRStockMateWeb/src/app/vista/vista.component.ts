import { AfterViewInit, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { EChartsOption } from 'echarts';
import { GridsterItem } from 'angular-gridster2';
import { GridItemServiceService } from '../services/grid-item-service.service';
import {AreaChartOptions, AreaChart_PerWarehouse, BarChartOptions, BarChart_Item, BarChart_RoutesPerVehicle, BubbleChartMaxLoadByModel, LineChartOptions, 
  LineChartRoutesByDate, 
  LineChart_ALL, LineChart_POOR, PieChartOptions, PieChartVehicleManufacturer, PieChart_TransportRoute_End, PieChart_TransportRoute_Start, RadarChartOptions, RadarChart_Routes } from '../interfaces/chart/DashboardView';
import { UserService } from '../services/user.service';
import { CompanyService } from '../services/company.service';
import { Company } from '../interfaces/company';
import { User } from '../interfaces/user';
import { TransportRoute } from '../interfaces/transport-route';
import { TransportRouteService } from '../services/transport-route.service';
import { Warehouse } from '../interfaces/warehouse';
import { Item } from '../interfaces/item';
import { ItemService } from '../services/item.service';
import { Communication } from '../interfaces/communication';
import { CommunicationService } from '../services/communication.service';
import { TransactionHistory } from '../interfaces/transaction-history';
import { TransactionsService } from '../services/transactions.service';
import { Message } from '../interfaces/message';
import { MessageService } from '../services/messages.service';
import { Vehicle } from '../interfaces/vehicle';


@Component({
  selector: 'app-vista',
  templateUrl: './vista.component.html',
  styleUrls: ['./vista.component.css']
})
export class VistaComponent {

  company!: Company;
  token: string = ""
  me!: User;
  users:User[] = [];
  routes:TransportRoute[] = [];
  items:Item[] = [];
  warehouses:Warehouse[] = [];
  communication: Communication[] = [];
  transactions: TransactionHistory[] = []
  messages: Message[] = [];
  vehicles: Vehicle[] = [];


  @Input() chartType: string = '';
  @Input() item!: GridsterItem;
  width: number = 500;
  height: number = 500

  chartOptions!: EChartsOption
  constructor(private itemService: ItemService, private gridItemService: GridItemServiceService,
    private userService: UserService, private companyService: CompanyService,
    private routeService: TransportRouteService,private communicationServices: CommunicationService,
    private transactionService: TransactionsService,private messagesServices: MessageService) { }

  ngOnInit(): void {
    this.getCompanyByUser()
    
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
        this.company = company;
        this.loadTransportRoute();
      }, error => {
        console.error('Error getting company by user:', error.message); // Aquí se imprime solo el mensaje de error
      });
  }
  loadEmployees(): void {
    this.companyService.getEmployees(this.company, this.token)
      .subscribe(employees => {
        this.users = employees;
        console.log(this.items)
        this.loadCharts()
      });
  }

  loadTransportRoute(): void {
    this.routeService.getTransportRoutesByCode(this.company.code, this.token)
      .subscribe(routesNew => {
        this.routes = routesNew
        this.loadWarehouse()
      }, error => {
      });
  }

  loadWarehouse(): void {
    this.companyService.getWarehouses(this.company, this.token)
      .subscribe(warehousesNew => {
        this.warehouses = warehousesNew
        this.loadItems()
      });
  }


  loadItems(): void {
    var stringT = sessionStorage.getItem('token')
    if (stringT) this.token = stringT

    this.itemService.getAllItems(this.token)
      .subscribe(items => {
        this.items = items
        this.loadCommunications()
      }
      );
  }

  loadCommunications() {
    this.communicationServices.getCommunicationsByCode(this.company.code, this.token).subscribe(comm => {
        this.communication = comm
       this.loadTransaction()

    })

  }
  loadTransaction() {
    this.transactionService.getHistory(this.company.code, this.token).subscribe(t => {
      this.transactions = t;
      this.loadMessages()
    })
  }


  loadMessages() {
    this.messagesServices.getMessagesByCode(this.company?.code, this.token)
      .subscribe(messagesNew => {
        this.messages = messagesNew;
        this.loadVehicles()
      });
  }

  loadVehicles(): void {
    this.companyService.getVehicles(this.company.code, this.token)
      .subscribe(vehicles => {
        this.vehicles = vehicles;
        this.loadEmployees()
      });
  }

  loadCharts(){
    switch (this.chartType) {
      case 'Chart: Trend Over Time':
        this.chartOptions =  LineChart_ALL(this.communication, this.transactions, this.messages);
        break;
      case 'Chart: Entities Per Warehouse':
        this.chartOptions = AreaChart_PerWarehouse(this.warehouses, this.items, this.routes);
        break;
      case 'Chart: Stock Quantity by Item':
        this.chartOptions = BarChart_Item(this.items);
        break;
      case 'Chart: Destination Routes per Warehouse':
        this.chartOptions = PieChart_TransportRoute_End(this.routes, this.warehouses);
        break;
      case 'Chart: Radar Chart of Transport Route Characteristics':
        this.chartOptions =  RadarChart_Routes(this.routes);
        break;
      case 'Chart: Transaction Trends Over Time':
        this.chartOptions = LineChart_POOR(this.transactions);
        break;
      case 'Chart: Communication Trends Over Time':
        this.chartOptions = LineChart_POOR(this.communication);
        break;
      case 'Chart: Message Trends Over Time':
        this.chartOptions = LineChart_POOR(this.messages);
        break;
      case 'Chart: Departure Routes per Warehouse':
        this.chartOptions = PieChart_TransportRoute_Start(this.routes, this.warehouses);
        break;
      case 'Chart: Routes per Vehicle':
          this.chartOptions =  BarChart_RoutesPerVehicle(this.vehicles,this.routes);
        break;
      case 'Chart: Vehicle Distribution by Manufacturer':
          this.chartOptions =  PieChartVehicleManufacturer(this.vehicles);
        break;
      case 'Chart: Relationship between Maximum Load and Vehicle Model':
          this.chartOptions =  BubbleChartMaxLoadByModel(this.vehicles);
        break;
      case 'Chart: Number of Transportation Routes by Date':
          this.chartOptions =  LineChartRoutesByDate(this.routes);
        break;
    }
  }
}