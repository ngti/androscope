import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Uri} from './uri';
import {RowCount} from './row-count';

@Injectable({
  providedIn: 'root'
})
export class RestService {

  private static BASE_URL = 'rest/';

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

  getData(uri: Uri, pageSize: number, pageNumber: number, sortColumn?: string, sortOrder?: string): Observable<[][]> {
    const dataParams = RestService.addParams(uri)
      .set('pageSize', pageSize.toString())
      .set('pageNumber', pageNumber.toString());
    if (sortColumn != null && sortOrder != null) {
      dataParams
        .set('sortColumn', sortColumn)
        .set('sortOrder', sortOrder);
    }
    return this.http.get<[][]>(RestService.BASE_URL + 'data', {
      params: dataParams
    });
  }
}
