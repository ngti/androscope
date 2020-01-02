import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HomeComponent} from './home/home.component';
import {ProviderComponent} from './provider/provider.component';
import {ProviderSuggestionsComponent} from './provider/provider-suggestions/provider-suggestions.component';
import {DatabaseListComponent} from './database/database-list/database-list.component';
import {DatabaseComponent} from './database/database.component';
import {DatabaseMetadataComponent} from './database/database-metadata/database-metadata.component';
import {DatabaseQueryComponent} from './database/database-query/database-query.component';
import {DatabaseTableComponent} from './database/database-table/database-table.component';
import {ProviderDataComponent} from './provider/provider-data/provider-data.component';

const providerChildRoutes: Routes = [
  {path: ':uri', component: ProviderDataComponent},
  {path: '', component: ProviderSuggestionsComponent}
];

const databasesChildRoutes: Routes = [
  {path: ':database/:query', component: DatabaseQueryComponent},
  {path: ':database/:table', component: DatabaseTableComponent},
  {path: ':database', component: DatabaseMetadataComponent},
  {path: '', component: DatabaseListComponent}
];

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'provider', component: ProviderComponent, children: providerChildRoutes},
  {path: 'database', component: DatabaseComponent, children: databasesChildRoutes},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
