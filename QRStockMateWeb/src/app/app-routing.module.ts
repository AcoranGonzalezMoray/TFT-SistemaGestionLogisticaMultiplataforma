import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { SignInComponent } from './sign-in/sign-in.component';


const routes: Routes = [
  {path:'', component:HomeComponent},
  {path:'login', component:SignInComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
