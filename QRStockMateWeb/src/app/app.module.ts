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
import { HistoryPanelComponent } from './history-panel/history-panel.component';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatTableModule} from '@angular/material/table';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatSelectModule} from '@angular/material/select';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { SignInComponent } from './sign-in/sign-in.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LottieModule } from "ngx-lottie";
import player from "lottie-web";
import { MainAnimationComponent } from './main-animation/main-animation.component';
import {MatStepperModule} from '@angular/material/stepper';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';

export function playerFactory() {
  return player;
}


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
    CommunicationPanelComponent,
    HistoryPanelComponent,
    SignInComponent,
    MainAnimationComponent
  ],
  imports: [
    BrowserModule,
    MatProgressSpinnerModule,
    LottieModule.forRoot({ player: playerFactory }),
    MatSelectModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatStepperModule,
    ReactiveFormsModule,
    FormsModule,
    MatIconModule,
    MatProgressBarModule,
    MatPaginatorModule,
    MatTableModule,
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
