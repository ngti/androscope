import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Uri} from '../query-model/uri';
import {SortDirection} from '@angular/material';
import {UriMetadata} from './uri-metadata';
import {FileSystemCount, FileSystemEntry} from './file-system-data';

class ParamsBuilder {

  private httpParams = new HttpParams();

  addUri(uri: Uri): ParamsBuilder {
    this.httpParams = this.httpParams.set('uri', uri.content);
    return this;
  }

  addFileSystemParams(type: FileSystemType, path?: string): ParamsBuilder {
    this.httpParams = this.httpParams.set('type', type);
    if (path != null) {
      this.httpParams = this.httpParams.set('path', path);
    }
    return this;
  }

  addPaginationParams(pageSize: number, pageNumber: number): ParamsBuilder {
    this.httpParams = this.httpParams
      .set('pageSize', pageSize.toString())
      .set('pageNumber', pageNumber.toString());
    return this;
  }

  addSorting(sortOrder: SortDirection, sortColumn?: string): ParamsBuilder {
    if (sortColumn != null && sortColumn.length > 0 && sortOrder.length > 0) {
      this.httpParams = this.httpParams
        .set('sortColumn', sortColumn)
        .set('sortOrder', sortOrder);
    }
    return this;
  }

  build(): HttpParams {
    return this.httpParams;
  }
}

export declare type FileSystemType =
  'application-data' |
  'external-storage' |
  'phone-root' |
  'downloads' |
  'photos' |
  'movies' |
  'pictures' |
  'music';

@Injectable({
  providedIn: 'root'
})
export class RestService {

  private static BASE_URL = 'http://10.10.4.88:8791/rest/';

  constructor(private http: HttpClient) {
  }

  getUriMetadata(uri: Uri): Observable<UriMetadata> {
    return this.http.get<UriMetadata>(RestService.BASE_URL + 'provider/metadata', {
      params: new ParamsBuilder()
        .addUri(uri)
        .build()
    });
  }

  getUriData(uri: Uri, pageSize: number, pageNumber: number, sortOrder: SortDirection, sortColumn?: string): Observable<[][]> {
    return this.http.get<[][]>(RestService.BASE_URL + 'provider/data', {
      params: new ParamsBuilder()
        .addUri(uri)
        .addPaginationParams(pageSize, pageNumber)
        .addSorting(sortOrder, sortColumn)
        .build()
    });
  }

  getFileList(
    type: FileSystemType, path: string, pageSize: number, pageNumber: number, sortOrder: SortDirection, sortColumn?: string
  ): Observable<FileSystemEntry[]> {
    return this.http.get<FileSystemEntry[]>(RestService.BASE_URL + 'file-system/list', {
      params: new ParamsBuilder()
        .addFileSystemParams(type, path)
        .addPaginationParams(pageSize, pageNumber)
        .addSorting(sortOrder, sortColumn)
        .build()
    });
  }

  getFileCount(type: FileSystemType, path?: string): Observable<FileSystemCount> {
    return this.http.get<FileSystemCount>(RestService.BASE_URL + 'file-system/count', {
      params: new ParamsBuilder()
        .addFileSystemParams(type, path)
        .build()
    });
  }
}
