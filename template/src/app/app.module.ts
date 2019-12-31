import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import {
  MatButtonModule, MatDividerModule,
  MatFormFieldModule, MatIconModule,
  MatInputModule, MatMenuModule,
  MatToolbarModule
} from '@angular/material';
import { QueryContentProviderComponent } from './query-content-provider/query-content-provider.component';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import { ToolbarComponent } from './navigation/toolbar/toolbar.component';
import { FooterComponent } from './navigation/footer/footer.component';

@NgModule({
  declarations: [
    AppComponent,
    QueryContentProviderComponent,
    ToolbarComponent,
    FooterComponent,
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
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
