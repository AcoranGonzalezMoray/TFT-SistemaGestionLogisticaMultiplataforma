import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { environment } from 'src/environment/environment';
import { Warehouse } from '../interfaces/warehouse';
import { TransactionHistory, me } from '../interfaces/transaction-history';
import { TransactionsService } from './transactions.service';

@Injectable({
  providedIn: 'root'
})
export class WarehouseService {

  private apiUrl: string = environment.API + '/Warehouse';

  constructor(private http: HttpClient, private transaction: TransactionsService) { }

  // Obtener todos los almacenes
  getAllWarehouses(): Observable<Warehouse[]> {
    return this.http.get<Warehouse[]>(this.apiUrl);
  }

  // Agregar un almacén
  addWarehouse(id: number, warehouse: Warehouse): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${id}`, warehouse);
  }

  // Actualizar un almacén
  updateWarehouse(warehouse: Warehouse, token:string): Observable<any> {
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    var transa:TransactionHistory = {
      id: 0,
      name: me()?.name ?? "Anonymous",
      code: me()?.code?? "000-000",
      description: `The ${warehouse.name} warehouse  has been deleted`,
      created: new Date(),
      operation: 2
    }

    this.transaction.create(transa,token).subscribe(()=>{
      console.log("buen")
    });


    return this.http.put<any>(this.apiUrl, warehouse,{headers: headers});
  }

  // Actualizar la imagen de un almacén
  updateWarehouseImage(warehouseId: number, image: File): Observable<any> {
    const formData = new FormData();
    formData.append('warehouseId', warehouseId.toString());
    formData.append('image', image);

    return this.http.post<any>(`${this.apiUrl}/UpdateImage`, formData);
  }


  addItemRange(token:string,itemModels: any[]): Observable<any> {
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    var transa:TransactionHistory = {
      id: 0,
      name: me()?.name ?? "Anonymous",
      code: me()?.code?? "000-000",
      description: `an item set with a total of ${itemModels.length} items has been added`,
      created: new Date(),
      operation: 0
    }

    this.transaction.create(transa,token).subscribe(()=>{
      console.log("buen")
    });

    return this.http.post<any>(`${this.apiUrl}/AddItemRange/`, itemModels, {headers:headers})
  }
  // Obtener todos los ítems de un almacén
  getItemsOfWarehouse(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/GetItems/${id}`);
  }
}
