import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {ProviderComponent} from './provider/provider.component';
import {ProviderSuggestionsComponent} from './provider/provider-suggestions/provider-suggestions.component';
import {DatabaseListComponent} from './database/database-list/database-list.component';
import {DatabaseComponent} from './database/database.component';
import {DatabaseMetadataComponent} from './database/database-metadata/database-metadata.component';
import {DatabaseQueryComponent} from './database/database-query/database-query.component';
import {DatabaseTableComponent} from './database/database-table/database-table.component';
import {ProviderDataComponent} from './provider/provider-data/provider-data.component';
import {FileExplorerComponent} from './file-explorer/file-explorer.component';
import {DatabaseQueryDataComponent} from './database/database-query-data/database-query-data.component';
import {DatabaseQueryEmptyComponent} from './database/database-query-empty/database-query-empty.component';
import {ImageCacheDataComponent} from './image-cache/image-cache-data/image-cache-data.component';

const providerChildRoutes: Routes = [
  {path: ':uri', component: ProviderDataComponent},
  {path: '', component: ProviderSuggestionsComponent}
];

const databaseQueryChildRoutes: Routes = [
  {path: '', component: DatabaseQueryEmptyComponent},
  {path: ':query', component: DatabaseQueryDataComponent},
];

const databasesChildRoutes: Routes = [
  {path: 'query', component: DatabaseQueryComponent, children: databaseQueryChildRoutes},
  {path: 'table/:table', component: DatabaseTableComponent},
  {path: '', component: DatabaseMetadataComponent}
];

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'provider', component: ProviderComponent, children: providerChildRoutes},
  {path: 'databases', component: DatabaseListComponent},
  {path: 'database/:database', component: DatabaseComponent, children: databasesChildRoutes},
  {path: 'file-explorer/:type', component: FileExplorerComponent},
  {path: 'image-cache/:type', component: ImageCacheDataComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
