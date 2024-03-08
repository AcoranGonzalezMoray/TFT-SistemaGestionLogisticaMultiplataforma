import { Component } from '@angular/core';
import { CompanyService } from '../services/company.service';
import { UserService } from '../services/user.service';
import { User } from '../interfaces/user';
import { Company } from '../interfaces/company';
import { MessageService } from '../services/messages.service';
import { Message } from '../interfaces/message';

@Component({
  selector: 'app-communication-panel',
  templateUrl: './communication-panel.component.html',
  styleUrls: ['./communication-panel.component.css']
})
export class CommunicationPanelComponent {

  isCommunicationClicked: boolean = false;
  token:string = ""
  users: User[] = [];
  messages: Message[] = [];

  me!:User;
  company!: Company;
  isLoading:Boolean = false
  userMessages: User[] = [];
  mainUserMessage?: User;

  constructor(private messagesServices: MessageService, private userService: UserService, private companyService: CompanyService) { }

  ngOnInit(): void {
    this.getCompanyByUser()
  }



  openMessages(user:User) {
    this.mainUserMessage = user;
    //this.messages filtra esos mensajes
  }

  onClickCommunicationButton() {
    this.isCommunicationClicked = !this.isCommunicationClicked;
  }
  verificarYAgregarSufijo(cadena: string): string {
    // Expresión regular para verificar si la cadena es una URL
    const regexURL = /^https?:\/\/(?:www\.)?[a-zA-Z0-9-]+\.[a-zA-Z]{2,}(?:\/[^\s]*)?$/;

    if (regexURL.test(cadena)) {
      return cadena;
    } else {
      return '../../assets/images/user.png';
    }
  }
  getCompanyByUser(): void {
    var stringT = sessionStorage.getItem('token')
    var stringU = sessionStorage.getItem('me')

    if(stringT && stringU){
      this.token = stringT;
      this.me = JSON.parse(stringU);
    }
    
    this.userService.getCompanyByUser(this.me, this.token)
    .subscribe(company => {
      setTimeout(() => {
        this.isLoading = false;
        this.company = company;
        this.loadEmployees();
      }, 1000);
      
    }, error => {
      console.error('Error getting company by user:', error.message); // Aquí se imprime solo el mensaje de error
    });
  }

  loadEmployees(): void {
    this.companyService.getEmployees(this.company, this.token)
      .subscribe(employees => {
        this.users = employees.map(employee => {
          employee.url = this.verificarYAgregarSufijo(employee.url); // Ajusta la asignación correctamente
          return employee;
        });
        this.users = this.users.concat(this.users);


        this.loadMessages()
      });
  }

  newMessage(user:User){
    this.userMessages.push(user)
  }

  loadMessages() {
    this.messagesServices.getMessagesByCode(this.company.code, this.token)
      .subscribe(messages => {
        this.me; // Soy el usuario
        const participantIds = new Set<number>();

        // Filtrar mensajes donde soy el remitente o el destinatario
        this.messages = messages.filter(message => 
          message.senderContactId === this.me.id || message.receiverContactId === this.me.id
        );


         // Obtener los IDs de los remitentes y destinatarios que no son el tuyo
        this.messages.forEach(message => {
          if (message.senderContactId !== this.me.id) {
            participantIds.add(message.senderContactId);
          }
          if (message.receiverContactId !== this.me.id) {
            participantIds.add(message.receiverContactId);
          }
        });

        this.userMessages = this.users.filter(u=> u.id in participantIds)
      });
  }
  
}


