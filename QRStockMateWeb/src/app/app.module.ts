import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { NgForOf } from '@angular/common';
import { GridsterComponent, GridsterItemComponent } from 'angular-gridster2';
import { VistaComponent } from './vista/vista.component';
import { DashboardsComponent } from './dashboards/dashboards.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { NavbarComponent } from './navbar/navbar.component';
import { HttpClientModule } from  '@angular/common/http';
import { NgxEchartsModule } from 'ngx-echarts';
import { UserPanelComponent } from './user-panel/user-panel.component';
import { CompanyPanelComponent } from './company-panel/company-panel.component';
import { ItemPanelComponent } from './item-panel/item-panel.component';
import { VehiclePanelComponent } from './vehicle-panel/vehicle-panel.component';
import { WarehousePanelComponent } from './warehouse-panel/warehouse-panel.component';
import { TransportRoutePanelComponent } from './transport-route-panel/transport-route-panel.component';
import { CommunicationPanelComponent } from './communication-panel/communication-panel.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {  MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    VistaComponent,
    DashboardsComponent,
    DashboardComponent,
    NavbarComponent,
    UserPanelComponent,
    CompanyPanelComponent,
    ItemPanelComponent,
    VehiclePanelComponent,
    WarehousePanelComponent,
    TransportRoutePanelComponent,
    CommunicationPanelComponent
  ],
  imports: [
    BrowserModule,
    MatIconModule,
    AppRoutingModule,
    NgForOf,
    GridsterComponent,
    GridsterItemComponent,
    HttpClientModule,
    NgxEchartsModule.forRoot({
      echarts: () => import('echarts'),
    }),
    BrowserAnimationsModule
    
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
