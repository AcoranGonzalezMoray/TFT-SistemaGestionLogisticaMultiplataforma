import { Component, ElementRef, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Item } from '../interfaces/item';
import { MatPaginator } from '@angular/material/paginator';
import { CompanyService } from '../services/company.service';
import { UserService } from '../services/user.service';
import { ItemService } from '../services/item.service';
import { rowsAnimation } from 'src/assets/animations';

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
  @ViewChild('notify') noty!: ElementRef;
  @ViewChild('notifEmpty') notE!: ElementRef;
  isLoading:Boolean = false

  constructor(private companyService: CompanyService, private userService:UserService, private itemService:ItemService) { }

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
}
