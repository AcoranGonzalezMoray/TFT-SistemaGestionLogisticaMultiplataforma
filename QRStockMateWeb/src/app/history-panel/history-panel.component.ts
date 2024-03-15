import { Component, ViewChild } from '@angular/core';
import { TransactionHistory } from '../interfaces/transaction-history';
import { MatTableDataSource } from '@angular/material/table';
import { User } from '../interfaces/user';
import { UserService } from '../services/user.service';
import { CompanyService } from '../services/company.service';
import { Company } from '../interfaces/company';
import { TransactionsService } from '../services/transactions.service';
import { MatPaginator } from '@angular/material/paginator';
import { rowsAnimation } from 'src/assets/animations';
import * as ExcelJS from 'exceljs';
import { baseImage } from 'src/assets/imagebase64';

@Component({
  selector: 'app-history-panel',
  templateUrl: './history-panel.component.html',
  styleUrls: ['./history-panel.component.css'],
  animations: [rowsAnimation]

})
export class HistoryPanelComponent {
  displayedColumns: string[] = ['id', 'name', 'code', 'description', 'created', 'operation', 'action'];
  dataSource = new MatTableDataSource<TransactionHistory>();
  clickedRows = new Set<TransactionHistory>();
  isLoading: Boolean = false
  token: string = ""
  me!: User;
  company!: Company;
  transactions: TransactionHistory[] = []
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  constructor(private userService: UserService, private transactionService: TransactionsService) { }

  ngOnInit(): void {
    this.isLoading = true
    this.getCompanyByUser()
  }




  setTransaction(transaction: TransactionHistory) {
    //this.transaction = { ...user };
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
        this.company = company;
        this.loadTransaction();
      }, error => {
        console.error('Error getting company by user:', error.message); // Aquí se imprime solo el mensaje de error
      });
  }


  loadTransaction() {
    this.transactionService.getHistory(this.company.code, this.token).subscribe(t => {
      setTimeout(() => {
        const ts: TransactionHistory[] = [];
        this.transactions = t;
        t.forEach((tr, index) => {
          setTimeout(() => {
            ts.push(tr);
            this.dataSource.data = ts;
          }, (index + 1) * 500);
        });

        this.dataSource.paginator = this.paginator;
        this.isLoading = false;
      }, 1000);
    })
  }


  searchByValue(element: HTMLInputElement) {
    this.dataSource.filter = element.value.trim().toLowerCase();
  }




  async export() {
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('Transaction History');

    // Definir los datos
    const headerRow = ['ID', 'Name', 'Code', 'Description', 'Created', 'Operation']; // Ajusta según los campos de la entidad

    // Agregar el logo
    worksheet.addRow([])
    worksheet.addRow([]);
    worksheet.addRow([]);
    worksheet.addRow([]);
    worksheet.addRow(headerRow);

    worksheet.mergeCells('A1:F4');
    // Obtener la celda fusionada
    const mergedCell = worksheet.getCell('A1');
    mergedCell.value = 'QRSTOCKMATE'
    mergedCell.fill = {
      type: 'pattern',
      pattern: 'solid',
      fgColor: { argb: 'FF5a79ba' } // Color #222222
    };

    this.transactions.forEach((transactionHistory: TransactionHistory) => {
      worksheet.addRow([
        transactionHistory.id,
        transactionHistory.name,
        transactionHistory.code,
        transactionHistory.description,
        transactionHistory.created,
        transactionHistory.operation
      ]);
    });

    // Establecer el tamaño de fuente y centrar el contenido de las celdas
    worksheet.eachRow({ includeEmpty: true }, (row, rowNumber) => {
      row.eachCell({ includeEmpty: true }, (cell, colNumber) => {
        cell.font = { size: 13 };
        cell.border = {
          top: { style: 'thin' },
          left: { style: 'thin' },
          bottom: { style: 'thin' },
          right: { style: 'thin' }
        };
        cell.alignment = { horizontal: 'center', vertical: 'middle' };
      });
    });
    worksheet.getRow(4).eachCell({ includeEmpty: false }, (cell, colNumber) => {
      cell.font = { bold: true }
    });
    worksheet.getRow(1).eachCell({ includeEmpty: false }, (cell, colNumber) => {
      cell.font = { bold: true, color: { argb: 'FFFFFFFF' } }; // Color blanco en hexadecimal
    });

    // Agregar imagen base64 como logo
    const base64Image = 'data:image/png;base64,' + baseImage; // Reemplaza baseImage con tu imagen base64
    const imageId = workbook.addImage({
      base64: base64Image,
      extension: 'png',
    });

    worksheet.addImage(imageId, "A1:A4");

    // Ajustar el ancho de las columnas según el tamaño del logo
    worksheet.columns.forEach((column, index) => {
      if (index === 3) { // Verifica si es la columna D (0-indexed)
        column.width = 90; // Ajusta el ancho de la columna D
      } else if (index === 4) {
        column.width = 50; // Ajusta el ancho de la columna D
      } else {
        column.width = 20; // Ajusta el ancho de las demás columnas
      }
    });

    // Guardar el archivo
    const buffer = await workbook.xlsx.writeBuffer();
    const blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    const fileName = 'QRSTOCKMATE_TransactionHistory_Report_' + new Date().toISOString() + '.xlsx';
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = fileName;
    link.click();
  }




}
