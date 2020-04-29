import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {ToolbarComponent} from './navigation/toolbar/toolbar.component';
import {FooterComponent} from './navigation/footer/footer.component';
import {HomeComponent} from './home/home.component';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatCardModule} from '@angular/material/card';
import {LayoutModule} from '@angular/cdk/layout';
import {ProviderComponent} from './provider/provider.component';
import {QueryDataComponent} from './common/query-data/query-data.component';
import {DatabaseComponent} from './database/database.component';
import {ProviderSuggestionsComponent} from './provider/provider-suggestions/provider-suggestions.component';
import {DatabaseListComponent} from './database/database-list/database-list.component';
import {DatabaseMetadataComponent} from './database/database-metadata/database-metadata.component';
import {DatabaseQueryComponent} from './database/database-query/database-query.component';
import {DatabaseTableComponent} from './database/database-table/database-table.component';
import {ProviderDataComponent} from './provider/provider-data/provider-data.component';
import {FileExplorerComponent} from './file-explorer/file-explorer.component';
import {DeleteConfirmationDialogComponent} from './file-explorer/delete-confirmation-dialog/delete-confirmation-dialog.component';
import {ImageCacheListComponent} from './image-cache/image-cache-list/image-cache-list.component';
import {ImageCacheGridComponent} from './image-cache/image-cache-grid/image-cache-grid.component';
import {DatabaseInfoComponent} from './database/database-info/database-info.component';
import {MatExpansionModule} from '@angular/material/expansion';
import {DatabaseQueryDataComponent} from './database/database-query-data/database-query-data.component';
import {DatabaseQueryEmptyComponent} from './database/database-query-empty/database-query-empty.component';
import {DatabaseUploadComponent} from './database/database-upload/database-upload.component';
import {DatabaseViewSqlComponent} from './database/database-view-sql/database-view-sql.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {MatDividerModule} from '@angular/material/divider';
import {MatListModule} from '@angular/material/list';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatDialogModule} from '@angular/material/dialog';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {ClipboardModule} from '@angular/cdk/clipboard';

@NgModule({
  declarations: [
    AppComponent,
    ToolbarComponent,
    FooterComponent,
    HomeComponent,
    ProviderComponent,
    QueryDataComponent,
    DatabaseComponent,
    ProviderSuggestionsComponent,
    DatabaseListComponent,
    DatabaseMetadataComponent,
    DatabaseQueryComponent,
    DatabaseTableComponent,
    ProviderDataComponent,
    FileExplorerComponent,
    DeleteConfirmationDialogComponent,
    ImageCacheListComponent,
    ImageCacheGridComponent,
    DatabaseInfoComponent,
    DatabaseQueryDataComponent,
    DatabaseQueryEmptyComponent,
    DatabaseUploadComponent,
    DatabaseViewSqlComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    MatToolbarModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule,
    MatGridListModule,
    MatCardModule,
    LayoutModule,
    MatListModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    MatExpansionModule,
    ClipboardModule,
  ],
  entryComponents: [
    DeleteConfirmationDialogComponent,
    DatabaseUploadComponent,
    DatabaseViewSqlComponent
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
