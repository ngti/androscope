import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Uri} from '../query-model/uri';
import {ProviderInfo} from './provider-info';
import {Breadcrumb, FileSystemCount, FileSystemEntry} from './file-system-data';
import {DataParams} from '../base/data-params';
import {FileSystemParams} from '../base/file-system-params';
import {Database, DatabaseInfo, SqlParams} from './database-data';
import {RequestResult} from './common-data';

class ParamsBuilder {

  private httpParams = new HttpParams();

  addUri(uri: Uri): ParamsBuilder {
    this.httpParams = this.httpParams
      .set('uri', uri.content)
      .set('timestamp', uri.timestamp.toString());
    return this;
  }

  addFileSystemParams(params: FileSystemParams): ParamsBuilder {
    this.httpParams = this.httpParams
      .set('type', params.fileSystemType)
      .set('timestamp', params.timestamp.toString());
    if (params.hasPath()) {
      this.httpParams = this.httpParams.set('path', params.encodedPath);
    }
    return this;
  }

  addDataParams(dataParams: DataParams): ParamsBuilder {
    this.httpParams = this.httpParams
      .set('pageSize', dataParams.pageSize.toString())
      .set('pageNumber', dataParams.pageNumber.toString());
    if (dataParams.hasSorting()) {
      this.httpParams = this.httpParams
        .set('sortColumn', dataParams.sortColumn)
        .set('sortOrder', dataParams.sortOrder);
    }
    return this;
  }

  addCustom(key: string, value: string) {
    this.httpParams = this.httpParams.set(key, value);
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

  private static ROOT = '';

  private static REST_URL = `${RestService.ROOT}rest/`;
  private static PROVIDER_URL = `${RestService.REST_URL}provider/`;
  private static FILE_SYSTEM_URL = `${RestService.REST_URL}file-system/`;
  private static DATABASE_URL = `${RestService.REST_URL}database/`;

  constructor(private http: HttpClient) {
  }

  private static getFileUrlParams(params: FileSystemParams): string {
    return '?' + new ParamsBuilder().addFileSystemParams(params).build().toString();
  }

  private static getUriUrlParams(uri: Uri): string {
    return '?' + new ParamsBuilder().addUri(uri).build().toString();
  }

  getProviderInfo(uri: Uri): Observable<ProviderInfo> {
    return this.http.get<ProviderInfo>(RestService.PROVIDER_URL + 'info', {
      params: new ParamsBuilder()
        .addUri(uri)
        .build()
    });
  }

  getUriData(uri: Uri, dataParams: DataParams): Observable<[][]> {
    return this.http.get<[][]>(RestService.PROVIDER_URL + 'data', {
      params: new ParamsBuilder()
        .addUri(uri)
        .addDataParams(dataParams)
        .build()
    });
  }

  getFileList(
    params: FileSystemParams, dataParams: DataParams
  ): Observable<FileSystemEntry[]> {
    return this.http.get<FileSystemEntry[]>(RestService.FILE_SYSTEM_URL + 'list', {
      params: new ParamsBuilder()
        .addFileSystemParams(params)
        .addDataParams(dataParams)
        .build()
    });
  }

  getFileCount(params: FileSystemParams): Observable<FileSystemCount> {
    return this.http.get<FileSystemCount>(RestService.FILE_SYSTEM_URL + 'count', {
      params: new ParamsBuilder()
        .addFileSystemParams(params)
        .build()
    });
  }

  getBreadcrumbs(params: FileSystemParams): Observable<Breadcrumb[]> {
    return this.http.get<Breadcrumb[]>(RestService.FILE_SYSTEM_URL + 'breadcrumbs', {
      params: new ParamsBuilder()
        .addFileSystemParams(params)
        .build()
    });
  }

  deleteFile(params: FileSystemParams): Observable<RequestResult> {
    return this.http.delete<RequestResult>(RestService.FILE_SYSTEM_URL + 'delete', {
      params: new ParamsBuilder()
        .addFileSystemParams(params)
        .build()
    });
  }

  getFileViewUrl(params: FileSystemParams): string {
    return RestService.FILE_SYSTEM_URL + 'view' + RestService.getFileUrlParams(params);
  }

  getFileDownloadUrl(params: FileSystemParams): string {
    return RestService.FILE_SYSTEM_URL + 'download' + RestService.getFileUrlParams(params);
  }

  getDatabaseList(): Observable<Database[]> {
    return this.http.get<Database[]>(RestService.DATABASE_URL + 'list');
  }

  getDatabaseTitle(uri: Uri): Observable<string> {
    return this.http.get<string>(RestService.DATABASE_URL + 'title', {
      params: new ParamsBuilder()
        .addUri(uri)
        .build()
    });
  }

  getDatabaseInfo(uri: Uri): Observable<DatabaseInfo> {
    return this.http.get<DatabaseInfo>(RestService.DATABASE_URL + 'info', {
      params: new ParamsBuilder()
        .addUri(uri)
        .build()
    });
  }

  canQuery(sql: string): Observable<boolean> {
    return this.http.post<boolean>(RestService.DATABASE_URL + 'can-query', new SqlParams(sql));
  }

  executeSql(uri: Uri, sql: string): Observable<RequestResult> {
    return this.http.post<RequestResult>(RestService.DATABASE_URL + 'execute-sql', new SqlParams(sql), {
      params: new ParamsBuilder()
        .addUri(uri)
        .build()
    });
  }

  getDatabaseDownloadUrl(uri: Uri): string {
    return RestService.DATABASE_URL + 'download' + RestService.getUriUrlParams(uri);
  }

  uploadDatabase(uri: Uri, file: File): Observable<RequestResult> {
    const formData = new FormData();
    formData.append(file.name, file);
    return this.http.post<RequestResult>(RestService.DATABASE_URL + 'upload', formData, {
      params: new ParamsBuilder()
        .addUri(uri)
        .build()
    });
  }

  getDatabaseSql(uri: Uri, name: string): Observable<string> {
    return this.http.get<string>(RestService.DATABASE_URL + 'sql', {
      params: new ParamsBuilder()
        .addUri(uri)
        .addCustom('name', name)
        .build()
    });
  }
}
