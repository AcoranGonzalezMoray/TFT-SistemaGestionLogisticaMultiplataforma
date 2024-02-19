import { Injectable } from '@angular/core';
import { GridsterItem } from 'angular-gridster2';
import { BehaviorSubject, Observable, Subject, of } from 'rxjs';
import { Dashboard } from '../interfaces/dashboard';

@Injectable({
  providedIn: 'root'
})
export class GridItemServiceService {

  private readonly storageKey = 'gridster-items-config';

  private  dashboard:Dashboard | undefined;
  private dashboardSubject: Subject<Dashboard> = new Subject<Dashboard>();
  private emptyGridsterItem: GridsterItem = {
    x: 0,
    y: 0,
    rows: 0,
    cols: 0,
    layerIndex: undefined,
    initCallback: undefined,
    dragEnabled: undefined,
    resizeEnabled: undefined,
    resizableHandles: {
        s: undefined,
        e: undefined,
        n: undefined,
        w: undefined,
        se: undefined,
        ne: undefined,
        sw: undefined,
        nw: undefined,
    },
    compactEnabled: undefined,
    maxItemRows: undefined,
    minItemRows: undefined,
    maxItemCols: undefined,
    minItemCols: undefined,
    minItemArea: undefined,
    maxItemArea: undefined,
};
 
  private size: BehaviorSubject<[number, number, GridsterItem]> = new BehaviorSubject<[number, number, GridsterItem]>([6, 6, this.emptyGridsterItem]);

  setDashboard(dashboard:Dashboard){
    this.dashboard =  dashboard
    this.dashboardSubject.next(dashboard); // Notificar el cambio a los suscriptores

  }

  getDashboard(){
    return this.dashboard
  }

  getDashboardObser(): Observable<Dashboard> {
    if (this.dashboard != undefined) {
      return this.dashboardSubject.asObservable();
    }
    return this.dashboardSubject.asObservable();
  }

  setSize(newSize: [number, number, GridsterItem]): void {
    this.size.next(newSize); // Actualizar el valor y notificar a los suscriptores
  }

  getSize(): Observable<[number, number, GridsterItem]> {
      return this.size.asObservable();
  }


  getItemsConfig(): Observable<GridsterItem[]> {
    const savedConfig = localStorage.getItem(this.storageKey);
    const itemsConfig: GridsterItem[] = savedConfig ? JSON.parse(savedConfig) : [];
    return of(itemsConfig);
  }

  saveItemsConfig(itemsConfig: GridsterItem[]): Observable<void> {
    localStorage.setItem(this.storageKey, JSON.stringify(itemsConfig));
    return of();
  }
}
