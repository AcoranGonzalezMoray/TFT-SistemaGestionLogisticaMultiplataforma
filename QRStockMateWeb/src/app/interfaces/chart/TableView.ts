// table-view.ts

import { DashboardView } from "./DashboardView";

export class TableView implements DashboardView {
  destroy(): unknown {
    throw new Error("Method not implemented.");
  }
  private tableData: any[] = [];
  private tableElement: HTMLElement | null = null;

  initializeView(chartContainer: HTMLElement): void {
    // Inicializar la tabla de datos con ngx-echarts o cualquier otra librería
    this.tableElement = chartContainer;
    console.log('Tabla de datos inicializada con ngx-echarts o tu librería preferida');
  }

  updateView(data: any): void {
    // Actualizar la tabla de datos con nuevos datos
    this.tableData = data;
    console.log('Tabla de datos actualizada con nuevos datos:', data);
    // Lógica para actualizar la tabla según tu librería preferida
  }
}
