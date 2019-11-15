import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Uri} from './uri';
import {RowCount} from './row-count';

@Injectable({
  providedIn: 'root'
})
export class RestService {

  constructor(private http: HttpClient) { }

  private static addUriParam(uri: Uri): string {
    return '?uri=' + encodeURIComponent(uri.content);
  }

  getColumns(uri: Uri): Observable<[]> {
    return this.http.get<[]>('rest/columns' + RestService.addUriParam(uri));
  }

  getRowCount(uri: Uri): Observable<RowCount> {
    return this.http.get<RowCount>('rest/row-count' + RestService.addUriParam(uri));
  }

  getData(uri: Uri): Observable<[][]> {
    return this.http.get<[][]>('rest/data' + RestService.addUriParam(uri));
  }
}
