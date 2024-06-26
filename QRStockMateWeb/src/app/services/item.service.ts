import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { environment } from 'src/environment/environment';
import { Item } from '../interfaces/item';
import { TransactionHistory, me, token } from '../interfaces/transaction-history';
import { TransactionsService } from './transactions.service';

@Injectable({
  providedIn: 'root'
})
export class ItemService {

  private apiUrl: string = environment.API + '/Item';

  constructor(private http: HttpClient, private transaction: TransactionsService) { }

  // Obtener todos los items
  getAllItems(token:string): Observable<Item[]> {
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<Item[]>(`${this.apiUrl}`, { headers: headers }).pipe(
      catchError(error => {
        throw 'Error al obtener los items: ' + error;
      })
    );
  }

  // Crear un nuevo item
  createItem(item: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}`, item).pipe(
      catchError(error => {
        throw 'Error al crear el item: ' + error;
      })
    );
  }

  // Actualizar un item existente
  updateItem(item: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}`, item).pipe(
      catchError(error => {
        throw 'Error al actualizar el item: ' + error;
      })
    );
  }

  // Eliminar un item
  deleteItem(item: any): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}`, { body: item }).pipe(
      catchError(error => {
        throw 'Error al eliminar el item: ' + error;
      })
    );
  }

  // Obtener items por nombre
  getItemsByName(name: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/Search/${name}`).pipe(
      catchError(error => {
        throw 'Error al obtener los items por nombre: ' + error;
      })
    );
  }

  // Actualizar imagen de un item
  updateItemImage(itemId: number, image: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('itemId', itemId.toString());
    formData.append('image', image, image.name);
    var transa:TransactionHistory = {
      id: 0,
      name: me()?.name ?? "Anonymous",
      code: me()?.code?? "000-000",
      description: `The image of item with ID ${itemId} has been modified`,
      created: new Date(),
      operation: 2
    }

    this.transaction.create(transa,token()).subscribe(()=>{
      console.log("buen")
    });

    return this.http.post<any>(`${this.apiUrl}/UpdateImage`, formData).pipe(
      catchError(error => {
        throw 'Error al actualizar la imagen del item: ' + error;
      })
    );
  }
}
