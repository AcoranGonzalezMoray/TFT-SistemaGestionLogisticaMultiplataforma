import { ChangeDetectorRef, Component, ElementRef, Type, ViewChild } from '@angular/core';
import { CompanyService } from '../services/company.service';
import { UserService } from '../services/user.service';
import { User, getRoleUser } from '../interfaces/user';
import { Company } from '../interfaces/company';
import { MessageService } from '../services/messages.service';
import { Message, TypeFile } from '../interfaces/message';
import { RoleStatus } from '../interfaces/transport-route';
import { CommunicationService } from '../services/communication.service';
import { Communication } from '../interfaces/communication';
declare var UIkit: any; // Importa UIKit

@Component({
  selector: 'app-communication-panel',
  templateUrl: './communication-panel.component.html',
  styleUrls: ['./communication-panel.component.css']
})


export class CommunicationPanelComponent {



  isCommunicationClicked: boolean = false;
  token: string = ""
  users: User[] = [];
  employees: User[] = [];
  messages: Message[] = [];
  communication: Communication[] = [];
  selectedColor: string = 'Low'; // Color predeterminado seleccionado
  colorOptions: string[] = ['Low', 'Medium', 'High'];

  Role = RoleStatus
  me!: User;
  company!: Company;
  isLoading: Boolean = false
  userMessages: User[] = [];
  tmpUserMessage: User[] = [];
  mainUserMessage?: User;
  mainMessage?: Message[];
  TypeFile = TypeFile;
  firstTime: boolean = true
  @ViewChild('chats') elementRef!: ElementRef;
  @ViewChild('noneS') elementRefS!: ElementRef;

  dragUIKIT: boolean = false;
  buttonRight: any;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private messagesServices: MessageService, private userService: UserService,
    private communicationServices: CommunicationService, private companyService: CompanyService) { }

  ngOnInit(): void {
    this.getCompanyByUser();
    setInterval(() => {
      if (!this.isCommunicationClicked) this.loadMessages();
      if (this.isCommunicationClicked) this.loadCommunications();

    }, 200);
  }
  dragUIKITView: boolean = false;

  ngAfterViewInit(): void {
    const myDraggableElement = this.elementRef.nativeElement.querySelector('#uikitsorteable');
    const myDraggableElementReemove = this.elementRefS.nativeElement.querySelector('#uikitsorteable2');
    console.log(myDraggableElementReemove)
    console.log(myDraggableElement)

    if (myDraggableElement) {
      UIkit.util.on(myDraggableElement, 'start', () => {
        this.dragUIKITView = true;
        setTimeout(() => {
          this.dragUIKIT = true;
        }, 200);
      });

      UIkit.util.on(myDraggableElement, 'stop', () => {
        this.dragUIKIT = false;
        setTimeout(() => {
          this.dragUIKITView = false;
        }, 800);
      });

      UIkit.util.on(myDraggableElementReemove, 'added', (event: any) => {
        const valorAriaDetails = event.detail[1].getAttribute('aria-details');
        const targetElement = event.target; // Obtener el elemento que disparó el evento
        const firstChild = targetElement.firstChild;

        // Eliminar todos los hijos del elemento excepto el primero
        while (targetElement.lastChild !== firstChild) {
          targetElement.removeChild(targetElement.lastChild);
        }
        this.removeMessages(parseInt(valorAriaDetails));
      });
    } else {
      console.error('Elemento #my_draggable_element no encontrado.');
    }

  }
  removeMessages(id: number) {
    console.log(id)
    this.messagesServices.deleteConversation(id.toString(), this.me.id.toString(), this.token)
      .subscribe(removed => {
        this.userMessages = this.userMessages.filter(x => x.id != id);
        this.tmpUserMessage = this.tmpUserMessage.filter(x => x.id != id);
        this.messages = this.messages.filter(message =>
          message.senderContactId !== id && message.receiverContactId !== id
        )
        this.changeDetectorRef.detectChanges(); // Actualiza la vista
      })
  }


  searchQuery: string = ''; // Define la propiedad searchQuery y inicialízala como una cadena vacía

  filterEmployee(filt: string) {
    filt = filt.toLowerCase(); // Convertir a minúsculas
    this.employees = this.users.filter(x => x.name.toLowerCase().includes(filt));
    if (filt.length == 0) this.employees = this.users;
  }


  toggleMove() {
    this.dragUIKIT = !this.dragUIKIT;
  }

  getRoleUser(number: any) {
    return getRoleUser(number);
  }

  @ViewChild('messagesContainer') messagesContainer!: ElementRef;

  // ...

  scrollToBottom(): void {
    setTimeout(() => {
      const container = this.messagesContainer.nativeElement;
      container.scrollTop = container.scrollHeight;
    }, 500)
  }

  onDropUIKIT(event: any) {
    console.log("dROP")
  }

  onDragOverUIKIT(event: any) {
    console.log("dRAG")
  }

  openMessages(user: User) {
    if (!this.isCommunicationClicked) {
      this.mainUserMessage = user;
      //this.messages filtra esos mensajes
      this.mainMessage = this.messages.filter(x =>
        x.receiverContactId == user.id && x.senderContactId == this.me.id
        ||
        x.receiverContactId == this.me.id && x.senderContactId == user.id
      );
      this.firstTime = false
    }
  }


  getNameByFirebase(url:string) {
    // Crear un objeto URL para facilitar el manejo de la URL
    const urlObj = new URL(url);

    // Obtener el pathname de la URL, que incluye la ruta del archivo y el nombre del archivo
    const pathname = urlObj.pathname;

    // Dividir el pathname usando el carácter '/' como separador y obtener el último elemento, que será el nombre del archivo
    const filename = pathname.split('/').pop();
    const decodedFilename = filename?decodeURIComponent(filename):'';

    return decodedFilename.split("_").pop();
  }




  onClickChatButton() {
    this.isCommunicationClicked = false
  }
  onClickCommunicationButton() {
    this.isCommunicationClicked = true
  }
  verificarYAgregarSufijo(cadena: string): string {
    // Expresión regular para verificar si la cadena es una URL
    const regexURL = /^https?:/;

    if (regexURL.test(cadena)) {
      return cadena;
    } else {
      return '../../assets/images/user.png';
    }
  }
  getCompanyByUser(): void {

    var stringT = sessionStorage.getItem('token')
    var stringU = sessionStorage.getItem('me')

    if (stringT && stringU) {
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

  loadCommunications() {
    this.communicationServices.getCommunicationsByCode(this.company.code, this.token).subscribe(comm => {
      // Filtrar mensajes que no están en this.messages y agregarlos
      comm.forEach(newMessage => {
        if (!this.communication.some(existingMessage => existingMessage.id === newMessage.id)) {
          this.communication.push(newMessage);
          this.scrollToBottom()
        }
      });
    })

  }

  loadEmployees(): void {
    this.companyService.getEmployees(this.company, this.token)
      .subscribe(employees => {
        this.users = employees.map(employee => {
          employee.url = this.verificarYAgregarSufijo(employee.url); // Ajusta la asignación correctamente
          return employee;
        });
        //this.users = this.users.concat(this.users);
        this.employees = this.users.filter(user => user.id != this.me.id);
        this.userMessages = employees

        this.loadMessages()
      });
  }

  newMessage(user: User) {
    // Verificar si el usuario ya está presente en userMessages
    const isUserAlreadyAdded = this.userMessages.some((existingUser) => existingUser.id === user.id);

    // Si el usuario no está en la lista, agrégalo
    if (!isUserAlreadyAdded) {
      this.tmpUserMessage.push(user);
      this.openMessages(user)
    }
  }

  loadMessages() {
    this.messagesServices.getMessagesByCode(this.company?.code, this.token)
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
        this.userMessages.push(...this.tmpUserMessage)
        this.mainUserMessage && this.firstTime == false ? this.openMessages(this.mainUserMessage) : null
        this.userMessages.length > 0 && this.firstTime == true ? (this.openMessages(this.userMessages[0])) : null;
      });
  }

  // Método para enviar un nuevo mensaje con el contenido proporcionado
  sendMessage(content: string, type?: TypeFile): void {
    if (!this.isCommunicationClicked) {
      // Verificar si hay un usuario seleccionado al que enviar el mensaje
      if (this.mainUserMessage != undefined) {
        // Crear un nuevo objeto Message con el contenido y los IDs de remitente y destinatario
        const newMessage: Message = {
          id: 0, // Esto se asignará en el backend
          code: this.company.code, // Esto se asignará en el backend
          senderContactId: this.me.id,
          receiverContactId: this.mainUserMessage.id,
          content: content,
          sentDate: new Date(),
          type: type ? type : TypeFile.Text // Tipo de archivo de texto
        };
        this.tmpUserMessage = this.tmpUserMessage.filter(x => x.id != this.mainUserMessage!.id,)
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
    } else if (this.isCommunicationClicked) {
      const index = this.colorOptions.indexOf(this.selectedColor);

      const communication: Communication = {
        id: 0,
        code: this.company.code,
        content: index + ';' + content,
        sentDate: new Date() // Esto asignará la fecha y hora actual
      };

      this.communicationServices.createCommunication(communication, this.token).subscribe(C => {

      })
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
      if (file.type === 'application/pdf' || file.type === 'image/jpeg' || file.type === 'image/png') {
        // Ejecutar la función de carga de archivos pasando el archivo
        this.uploadFile(file);
      } else {
        // Mostrar alerta si el archivo no es un PDF
        alert('Documento no permitido. Por favor, asegúrate de arrastrar un archivo PDF.');
      }
    }
  }

  uploadFile(file: File) {
    console.log('Archivo cargado:', file);

    if (this.mainUserMessage != undefined) {
      // Crear un nuevo objeto Message con el contenido y los IDs de remitente y destinatario
      const newMessage: Message = {
        id: 0, // Esto se asignará en el backend
        code: this.company.code, // Esto se asignará en el backend
        senderContactId: this.me.id,
        receiverContactId: this.mainUserMessage.id,
        content: 'content',
        sentDate: new Date(),
        type: file.type === 'application/pdf' ? TypeFile.File : TypeFile.Image // Tipo de archivo de texto
      };
      this.tmpUserMessage = this.tmpUserMessage.filter(x => x.id != this.mainUserMessage!.id,)
      // Llamar al servicio para crear el nuevo mensaje
      this.messagesServices.uploadFile(file, newMessage, this.token)
        .subscribe(response => {
          console.error('Exitoso');
        }, error => {
          console.error('Error al enviar el mensaje:', error);
          // Aquí puedes manejar los errores si es necesario
        });
    }

  }


}


