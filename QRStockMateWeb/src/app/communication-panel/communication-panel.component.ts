import { Component, ElementRef, ViewChild } from '@angular/core';
import { CompanyService } from '../services/company.service';
import { UserService } from '../services/user.service';
import { User } from '../interfaces/user';
import { Company } from '../interfaces/company';
import { MessageService } from '../services/messages.service';
import { Message, TypeFile } from '../interfaces/message';

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
  mainMessage?: Message[];
  TypeFile= TypeFile;
  firstTime:boolean = true
  constructor(private messagesServices: MessageService, private userService: UserService, private companyService: CompanyService) { }

  ngOnInit(): void {
    this.getCompanyByUser();

    setInterval(() => {
        this.loadMessages(); 
    }, 200);

}

@ViewChild('messagesContainer') messagesContainer!: ElementRef;

// ...

  scrollToBottom(): void {
      setTimeout(()=>{
        const container = this.messagesContainer.nativeElement;
        container.scrollTop = container.scrollHeight;
      }, 500)
  }

  openMessages(user:User) {
    this.mainUserMessage = user;
    //this.messages filtra esos mensajes
    this.mainMessage = this.messages.filter(x=>
      x.receiverContactId == user.id && x.senderContactId == this.me.id
      ||
      x.receiverContactId == this.me.id && x.senderContactId == user.id
      );
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
        this.userMessages = employees

        this.loadMessages()
      });
  }

  newMessage(user: User) {
    // Verificar si el usuario ya está presente en userMessages
    const isUserAlreadyAdded = this.userMessages.some((existingUser) => existingUser.id === user.id);

    // Si el usuario no está en la lista, agrégalo
    if (!isUserAlreadyAdded) {
        this.userMessages.push(user);
    }
}

  loadMessages() {
    this.messagesServices.getMessagesByCode(this.company.code, this.token)
        .subscribe(messagesNew => {
            this.me; // Soy el usuario
            const participantIds = new Set<number>();

            // Filtrar mensajes donde soy el remitente o el destinatario
            var tmp = messagesNew.filter(message =>
              message.senderContactId === this.me.id || message.receiverContactId === this.me.id
            );

            // Filtrar mensajes que no están en this.messages y agregarlos
            tmp.forEach(newMessage => {
                if (!this.messages.some(existingMessage => existingMessage.id === newMessage.id)) {
                    this.messages.push(newMessage);
                    this.scrollToBottom()

                }
            });



            // Obtener los IDs de los remitentes y destinatarios que no son el tuyo
            this.messages.forEach(message => {
                if (message.senderContactId !== this.me.id) {
                    participantIds.add(message.senderContactId);
                }
                if (message.receiverContactId !== this.me.id) {
                    participantIds.add(message.receiverContactId);
                }
            });

             // Filtrar los usuarios para que solo estén aquellos cuyos IDs estén en participantIds
             const userMap: { [key: number]: boolean } = {}; // Objeto para evitar duplicados
             this.userMessages = [];
             this.users.forEach(user => {
                 if (participantIds.has(user.id) && !userMap[user.id]) {
                     this.userMessages.push(user);
                     userMap[user.id] = true; // Marcar el ID del usuario como agregado
                 }
             });
             this.mainUserMessage && this.firstTime == false?this.openMessages( this.mainUserMessage ) :null
             this.userMessages.length > 0 && this.firstTime ? (this.openMessages(this.userMessages[0])) : null;
             this.firstTime = false
          });
  }

// Método para enviar un nuevo mensaje con el contenido proporcionado
sendMessage(content: string): void {
    // Verificar si hay un usuario seleccionado al que enviar el mensaje
  if (this.mainUserMessage) {
      // Crear un nuevo objeto Message con el contenido y los IDs de remitente y destinatario
      const newMessage: Message = {
        id: 0, // Esto se asignará en el backend
        code: this.company.code, // Esto se asignará en el backend
        senderContactId: this.me.id,
        receiverContactId: this.mainUserMessage.id,
        content: content,
        sentDate: new Date(),
        type: TypeFile.Text // Tipo de archivo de texto
      };

      // Llamar al servicio para crear el nuevo mensaje
      this.messagesServices.createMessage(newMessage, this.token)
        .subscribe(response => {
        }, error => {
          console.error('Error al enviar el mensaje:', error);
          // Aquí puedes manejar los errores si es necesario
        });
    } else {
      console.warn('Ningún usuario seleccionado para enviar el mensaje.');
      // Aquí puedes manejar la situación donde ningún usuario está seleccionado para enviar el mensaje
    }
  }


  // Agrega esto en tu componente TypeScript
  draggingOver = false;

  handleDragOver(event: DragEvent) {
      event.preventDefault();
  }

  handleDragEnter(event: DragEvent) {
      event.preventDefault();
      this.draggingOver = true;
  }

  handleDragLeave(event: DragEvent) {
      event.preventDefault();
      this.draggingOver = false;
  }

  handleDrop(event: DragEvent) {
      event.preventDefault();
      this.draggingOver = false;
      const files = event.dataTransfer?.files;
      if (files && files.length > 0) {
          // Obtener el primer archivo (asumiendo que solo se permite arrastrar un archivo a la vez)
          const file = files[0];
          // Ejecutar la función de carga de archivos pasando el archivo
          this.uploadFile(file);
      }
  }

  uploadFile(file: File) {
      // Aquí puedes implementar la lógica para subir el archivo
      // Por ejemplo, puedes usar una función de servicio para subir el archivo al servidor
      console.log('Archivo cargado:', file);
  }


}


