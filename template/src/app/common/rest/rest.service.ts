import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Uri} from '../query-model/uri';
import {SortDirection} from '@angular/material';
import {UriMetadata} from './uri-metadata';

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

  getUriMetadata(uri: Uri): Observable<UriMetadata> {
    return this.http.get<UriMetadata>(RestService.BASE_URL + 'metadata', {
      params: RestService.addParams(uri)
    });
  }

  getUriData(uri: Uri, pageSize: number, pageNumber: number, sortOrder: SortDirection, sortColumn?: string): Observable<[][]> {
    let dataParams = RestService.addParams(uri)
      .set('pageSize', pageSize.toString())
      .set('pageNumber', pageNumber.toString());
    if (sortColumn != null && sortColumn.length > 0 && sortOrder.length > 0) {
      dataParams = dataParams
        .set('sortColumn', sortColumn)
        .set('sortOrder', sortOrder);
    }
    return this.http.get<[][]>(RestService.BASE_URL + 'data', {
      params: dataParams
    });
  }
}
