import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {QueryContentProviderComponent} from './query-content-provider/query-content-provider.component';


const routes: Routes = [
  {path: '', component: QueryContentProviderComponent},
  {path: ':uri', component: QueryContentProviderComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
