import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {Uri} from '../query-model/uri';
import {RowCount} from './row-count';
import {SortDirection} from '@angular/material';

@Injectable({
  providedIn: 'root'
})
export class RestService {

  private static BASE_URL = 'http://10.10.4.88:8791/rest/';

  constructor(private http: HttpClient) {
  }

  private static addParams(uri: Uri): HttpParams {
    return new HttpParams().set('uri', uri.content);
  }

  getColumns(uri: Uri): Observable<[]> {
    return this.http.get<[]>(RestService.BASE_URL + 'columns', {
      params: RestService.addParams(uri)
    });
  }

  getRowCount(uri: Uri): Observable<RowCount> {
    return this.http.get<RowCount>(RestService.BASE_URL + 'row-count', {
      params: RestService.addParams(uri)
    });
  }

  getData(uri: Uri, pageSize: number, pageNumber: number, sortOrder: SortDirection, sortColumn?: string): Observable<[][]> {
    let dataParams = RestService.addParams(uri)
      .set('pageSize', pageSize.toString())
      .set('pageNumber', pageNumber.toString());
    console.log(`sortOrder: ${sortOrder}, sortColumn: ${sortColumn}`);
    if (sortColumn != null && sortColumn.length > 0 && sortOrder.length > 0) {
      console.log('Adding sort params');
      dataParams = dataParams
        .set('sortColumn', sortColumn)
        .set('sortOrder', sortOrder);
    }
    console.log('Params: ' + dataParams);
    return this.http.get<[][]>(RestService.BASE_URL + 'data', {
      params: dataParams
    });
  }
}
