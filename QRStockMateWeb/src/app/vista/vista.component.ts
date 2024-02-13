import { AfterViewInit, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { EChartsOption } from 'echarts';
import { GridsterItem } from 'angular-gridster2';
import { GridItemServiceService } from '../services/grid-item-service.service';
import {AreaChartOptions, BarChartOptions, LineChartOptions, PieChartOptions, RadarChartOptions } from '../interfaces/chart/DashboardView';


@Component({
  selector: 'app-vista',
  templateUrl: './vista.component.html',
  styleUrls: ['./vista.component.css']
})
export class VistaComponent{
 

  @Input() chartType: string = '';
  @Input() item!: GridsterItem;
  width:number = 500;
  height:number = 500

  chartOptions!: EChartsOption 
  constructor(private gridItemService: GridItemServiceService) {}

  ngOnInit(): void {
    switch (this.chartType) {
      case 'A':
        this.chartOptions =  LineChartOptions();
        break;
      case 'B':
        this.chartOptions =  AreaChartOptions();
        break;
      case 'C':
        this.chartOptions =  BarChartOptions();
        break;
      case 'D':
        this.chartOptions =  PieChartOptions();
        break;
      case 'E':
        this.chartOptions =  RadarChartOptions();
        break;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['item']) {
      console.log('Nuevo tamaño del elemento:', this.width, 'x', this.height);
      this.handleItemChange();
    }
  }

  private handleItemChange(): void {


    this.gridItemService.getSize().subscribe(size => {
      if(size[2] == this.item){

      this.width = size[0] // Ajustar el ancho según el número de columnas
      this.height = size[1] // Ajustar la altura según el número de filas

      // Imprimir para verificar el cambio en las dimensiones
      console.log('Nuevo tamaño del elemento:', this.width, 'x', size);
      }
    })
  }
  
}
  

