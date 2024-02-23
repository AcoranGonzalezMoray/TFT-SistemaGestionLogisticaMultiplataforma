import { Component, ElementRef, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Item } from '../interfaces/item';
import { MatPaginator } from '@angular/material/paginator';
import { ItemService } from '../services/item.service';
import { rowsAnimation } from 'src/assets/animations';
import * as ExcelJS from 'exceljs';
import { baseImage } from 'src/assets/imagebase64';
import * as XLSX from 'xlsx';

@Component({
  selector: 'app-item-panel',
  templateUrl: './item-panel.component.html',
  styleUrls: ['./item-panel.component.css'],
  animations: [rowsAnimation]
})

export class ItemPanelComponent {

  displayedColumns: string[] = ['id', 'name', 'warehouseId', 'location', 'stock', 'weightPerUnit', 'action'];
  dataSource = new MatTableDataSource<Item>();
  token:string = ""
  item:Item|undefined;
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;
  clickedRows = new Set<Item>();
  @ViewChild('notifyI') noty!: ElementRef;
  @ViewChild('notifyError') notyError!: ElementRef;
  @ViewChild('notifEmptyI') notE!: ElementRef;
  isLoading:Boolean = false
  items:Item[]= []
  messages = ""

  itemsExcel: Item[] = []
  itemsExcelError: [String, Number, Number][] = []

  isWeight = 3
  isStock = 3
  isName = 3
  isWarehouse= 3
  isLocation= 3

  constructor(private itemService:ItemService) { }

  ngOnInit(): void {
    this.isLoading = true
    this.loadItems();
    
  }

  setItem(item:Item){
    this.item = item
  }

  searchByValue(element:HTMLInputElement){
    this.dataSource.filter = element.value.trim().toLowerCase();

  }

  loadItems(): void {
    var stringT = sessionStorage.getItem('token')
    if (stringT) this.token = stringT
    
    this.itemService.getAllItems(this.token)
      .subscribe(items => {
        const itemC: Item[] = [];

        this.items = items
        items.forEach((i: Item, index: number) => {
          setTimeout(() => {
            itemC.push(i);
            this.dataSource.data = itemC;
          }, (index + 1) * 500); 
        });
        setTimeout(() => {
          this.isLoading = false
        }, 1000);
        this.dataSource.paginator = this.paginator; 
        
      }, error => {
        setTimeout(() => {
          this.isLoading = false
          this.notE.nativeElement.click()
        }, 1000);
      });
  }




  async export() {
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('Items');

    // Definir los datos
    const headerRow = ['ID', 'Name', 'Warehouse ID', 'Location', 'Stock', 'Weight Per Unit (Kg)'];

    // Agregar el logo
    worksheet.addRow([])
    worksheet.addRow([]);
    worksheet.addRow([]);
    worksheet.addRow([]);
    worksheet.addRow(headerRow);

    worksheet.mergeCells('A1:F4');
    // Obtener la celda fusionada
    const mergedCell = worksheet.getCell('A1');
    mergedCell.value  = 'QRSTOCKMATE'
    mergedCell.fill = {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: 'FF5a79ba' } // Color #222222
    };


    this.items.forEach((item: Item) => {
      // Agregar una fila para cada ítem
      worksheet.addRow([
          item.id,
          item.name,
          item.warehouseId,
          item.location,
          item.stock,
          item.weightPerUnit
      ]);
    });

    // Establecer el tamaño de fuente y centrar el contenido de las celdas
    worksheet.eachRow({ includeEmpty: true }, (row, rowNumber) => {
        row.eachCell({ includeEmpty: true }, (cell, colNumber) => {
            cell.font = { size: 13 };
            cell.border = {
              top: {style:'thin'},
              left: {style:'thin'},
              bottom: {style:'thin'},
              right: {style:'thin'}
            };
            cell.alignment = { horizontal: 'center', vertical: 'middle' };
        });
    });
    worksheet.getRow(4).eachCell({ includeEmpty: false }, (cell, colNumber) => {
      cell.font = {bold:true}
    });
    worksheet.getRow(1).eachCell({ includeEmpty: false }, (cell, colNumber) => {
      cell.font = { bold: true, color: { argb: 'FFFFFFFF' } }; // Color blanco en hexadecimal
    });

    // Calcular el ancho total de las columnas del encabezado
    const totalWidth = headerRow.reduce((acc, curr) => {
        return acc + (curr.length * 1.2); // Ajusta el factor multiplicador según sea necesario
    }, 0);

    // Agregar imagen base64 como logo
    const base64Image = 'data:image/png;base64,' + baseImage; // Reemplaza baseImage con tu imagen base64
    const imageId = workbook.addImage({
        base64: base64Image,
        extension: 'png',
    });

    worksheet.addImage(imageId, "A1:A4");

    // Ajustar el ancho de las columnas según el tamaño del logo
    worksheet.columns.forEach((column, index) => {
      if (index === 5) { // Verifica si es la columna D (0-indexed)
          column.width = 30; // Ajusta el ancho de la columna D
      } else {
          column.width = 20; // Ajusta el ancho de las demás columnas
      }
    });

    // Guardar el archivo
    const buffer = await workbook.xlsx.writeBuffer();
    const blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    const fileName = 'QRSTOCKMATE_Items_Report_' + new Date().toISOString() + '.xlsx';
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = fileName;
    link.click();
  }




  onFileChange(event: any, header:number) {

    this.isWeight = 0
    this.isStock = 0
    this.isName = 0
    this.isWarehouse= 0
    this.isLocation= 0

    this.itemsExcel = []

    const file = event?.target?.files[0];
    console.log(file);
    if (file) {
      const workbook = new ExcelJS.Workbook();
      workbook.xlsx.load(file).then(() => {
        const worksheet = workbook.getWorksheet(1); // Obtener la primera hoja del libro
        const columnIndexes: { [key: string]: number } = {
          'Name': -1,
          'Warehouse ID': -1,
          'Location': -1,
          'Stock': -1,
          'Weight Per Unit (Kg)': -1
        };
        let allColumnsPresent = true;
        worksheet?.eachRow((row, rowNumber) => {
          if (rowNumber == header) { // Identificar el índice de cada columna requerida
            row.eachCell((cell, colNumber) => {
              if(cell.value){
                const columnName = cell.value.toString();
                if (columnIndexes.hasOwnProperty(columnName)) {
                  columnIndexes[columnName] = colNumber;
                }
              }
            });
            // Verificar si todas las columnas están presentes
            let index = 0;
            for (const column of Object.keys(columnIndexes)) {
              setTimeout(() => {
                if (columnIndexes[column] === -1) {
                  if(column == 'Name')this.isName = 2
                  if(column == 'Warehouse ID')this.isWarehouse = 2
                  if(column == 'Location')this.isLocation = 2
                  if(column == 'Stock')this.isStock = 2
                  if(column == 'Weight Per Unit (Kg)') this.isWeight = 2
  
                  console.error(`La columna '${column}' no está presente en el archivo Excel.`);
                  this.messages = `La columna '${column}' no está presente en el archivo Excel.`
                  //this.notyError.nativeElement.click()
                  allColumnsPresent = false;
                }else {
                  if(column == 'Name')this.isName = 1
                  if(column == 'Warehouse ID')this.isWarehouse = 1
                  if(column == 'Location')this.isLocation = 1
                  if(column == 'Stock')this.isStock = 1
                  if(column == 'Weight Per Unit (Kg)') this.isWeight = 1
                }
              },700*  ++index)
            }
          } else { // Crear objetos Item y agregarlos a la lista si todas las columnas están presentes
            if (allColumnsPresent) {
              const name = String(row.getCell(columnIndexes['Name']).value);
              const warehouseId = Number(row.getCell(columnIndexes['Warehouse ID']).value);
              const location = String(row.getCell(columnIndexes['Location']).value?.toString());
              const stock = Number(row.getCell(columnIndexes['Stock']).value);
              const weightPerUnit = Number(row.getCell(columnIndexes['Weight Per Unit (Kg)']).value);

              // Verificar si algún campo es NaN
              if (!isNaN(warehouseId) && !isNaN(stock) && !isNaN(weightPerUnit)) {
                const newItem: Item = {
                  id: 0,
                  name: name,
                  warehouseId: warehouseId,
                  location: location,
                  stock: stock,
                  url: '', // Debes definir cómo obtener este valor del archivo Excel
                  weightPerUnit: weightPerUnit
                };
                
                this.itemsExcel.push(newItem);
              } else {
                console.error(`Valor NaN detectado en la fila ${rowNumber}, columna ${columnIndexes['Name']+1}`);
                this.itemsExcelError.push([name,rowNumber, columnIndexes['Name']+1 ])
              }
            }
          }
        });
      });
    }
  }
  

}
