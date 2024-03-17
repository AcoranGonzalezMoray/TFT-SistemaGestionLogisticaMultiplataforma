import { Component, Input } from '@angular/core';
import { ItemService } from '../services/item.service';
import { Communication } from '../interfaces/communication';
import { Company } from '../interfaces/company';
import { Item } from '../interfaces/item';
import { Message } from '../interfaces/message';
import { TransactionHistory } from '../interfaces/transaction-history';
import { TransportRoute } from '../interfaces/transport-route';
import { User } from '../interfaces/user';
import { Vehicle } from '../interfaces/vehicle';
import { Warehouse } from '../interfaces/warehouse';
import { CommunicationService } from '../services/communication.service';
import { CompanyService } from '../services/company.service';
import { GridItemServiceService } from '../services/grid-item-service.service';
import { MessageService } from '../services/messages.service';
import { TransactionsService } from '../services/transactions.service';
import { TransportRouteService } from '../services/transport-route.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-count',
  templateUrl: './count.component.html',
  styleUrls: ['./count.component.css']
})
export class CountComponent {
  company!: Company;
  token: string = ""
  me!: User;
  users: User[] = [];
  routes: TransportRoute[] = [];
  items: Item[] = [];
  warehouses: Warehouse[] = [];
  communication: Communication[] = [];
  transactions: TransactionHistory[] = []
  messages: Message[] = [];
  vehicles: Vehicle[] = [];


  @Input() title!: string;
  count: number = 500;

  currentCount: number = 0;
  constructor(private itemService: ItemService, private gridItemService: GridItemServiceService,
    private userService: UserService, private companyService: CompanyService,
    private routeService: TransportRouteService, private communicationServices: CommunicationService,
    private transactionService: TransactionsService, private messagesServices: MessageService) { }

  ngOnInit() {
    this.getCompanyByUser();
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
        switch (this.title) {
          case "Count: Number of Employees":
            this.loadEmployees()
            break;
          case "Count: Number of Vehicles":
            this.loadVehicles()
            break;
          case "Count: Number of Warehouses":
            this.loadWarehouse()
            break;
          case "Count: Number of Items":
            this.loadItems()
            break;
          case "Count: Number of Communications":
            this.loadCommunications()
            break;
          case "Count: Number of Transport Routes":
            this.loadTransportRoute()
            break
          default:
            break;
        }





      }, error => {
        console.error('Error getting company by user:', error.message); // Aquí se imprime solo el mensaje de error
      });
  }
  loadEmployees(): void {
    this.companyService.getEmployees(this.company, this.token)
      .subscribe(employees => {
        this.users = employees;
        this.count = this.users.length
        this.animateCount()

      });
  }

  loadTransportRoute(): void {
    this.routeService.getTransportRoutesByCode(this.company.code, this.token)
      .subscribe(routesNew => {
        this.routes = routesNew
        this.count = this.routes.length
        this.animateCount()
      }, error => {
      });
  }

  loadWarehouse(): void {
    this.companyService.getWarehouses(this.company, this.token)
      .subscribe(warehousesNew => {
        this.warehouses = warehousesNew
        this.count = this.warehouses.length
        this.animateCount()
      });
  }


  loadItems(): void {
    var stringT = sessionStorage.getItem('token')
    if (stringT) this.token = stringT

    this.itemService.getAllItems(this.token)
      .subscribe(items => {
        this.items = items
        this.count = this.items.length
        this.animateCount()
      }
      );
  }

  loadCommunications() {
    this.communicationServices.getCommunicationsByCode(this.company.code, this.token).subscribe(comm => {
      this.communication = comm
      this.count = this.communication.length
      this.animateCount()
    })

  }
  loadTransaction() {
    this.transactionService.getHistory(this.company.code, this.token).subscribe(t => {
      this.transactions = t;
    })
  }


  loadMessages() {
    this.messagesServices.getMessagesByCode(this.company?.code, this.token)
      .subscribe(messagesNew => {
        this.messages = messagesNew;
      });
  }

  loadVehicles(): void {
    this.companyService.getVehicles(this.company.code, this.token)
      .subscribe(vehicles => {
        this.vehicles = vehicles;
        this.count = this.vehicles.length
        this.animateCount()
      });
  }

  animateCount() {
    const steps = 50; // Número de pasos de la animación
    const increment = this.count / steps; // Valor de incremento en cada paso

    let currentStep = 0;
    const timerInterval = 1000 / steps; // Intervalo de tiempo entre cada paso (en milisegundos)

    const timer = setInterval(() => {
      this.currentCount = Math.round(increment * currentStep); // Redondear el valor para evitar decimales
      currentStep++;

      if (currentStep >= steps) {
        clearInterval(timer);
        this.currentCount = this.count; // Asegúrate de que el conteo final sea exactamente el valor de `count`
      }
    }, timerInterval);
  }

}
