import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {QueryContentProviderComponent} from './query-content-provider/query-content-provider.component';


const routes: Routes = [
  {path: '', redirectTo: '/provider', pathMatch: 'full'},
  {path: 'provider', component: QueryContentProviderComponent},
  {path: 'provider/:uri', component: QueryContentProviderComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
