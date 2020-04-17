import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Uri} from '../query-model/uri';
import {UriMetadata} from './uri-metadata';
import {Breadcrumb, FileDeleteResult, FileSystemCount, FileSystemEntry} from './file-system-data';
import {DataParams} from '../base/data-params';

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

  private static ROOT = 'http://192.168.178.10:8791/';

  private static REST_URL = `${RestService.ROOT}rest/`;

  private static VIEW_URL = `${RestService.ROOT}view`;

  private static DOWNLOAD_URL = `${RestService.ROOT}download`;

  constructor(private http: HttpClient) {
  }

  private static getFileUrlParams(type: FileSystemType, path?: string): string {
    return '?' + new ParamsBuilder().addFileSystemParams(type, path).build().toString();
  }

  getUriMetadata(uri: Uri): Observable<UriMetadata> {
    return this.http.get<UriMetadata>(RestService.REST_URL + 'provider/metadata', {
      params: new ParamsBuilder()
        .addUri(uri)
        .build()
    });
  }

  getUriData(uri: Uri, dataParams: DataParams): Observable<[][]> {
    return this.http.get<[][]>(RestService.REST_URL + 'provider/data', {
      params: new ParamsBuilder()
        .addUri(uri)
        .addDataParams(dataParams)
        .build()
    });
  }

  getFileList(
    type: FileSystemType, path: string, dataParams: DataParams
  ): Observable<FileSystemEntry[]> {
    return this.http.get<FileSystemEntry[]>(RestService.REST_URL + 'file-system/list', {
      params: new ParamsBuilder()
        .addFileSystemParams(type, path)
        .addDataParams(dataParams)
        .build()
    });
  }

  getFileCount(type: FileSystemType, path?: string): Observable<FileSystemCount> {
    return this.http.get<FileSystemCount>(RestService.REST_URL + 'file-system/count', {
      params: new ParamsBuilder()
        .addFileSystemParams(type, path)
        .build()
    });
  }

  getBreadcrumbs(type: FileSystemType, path?: string): Observable<Breadcrumb[]> {
    return this.http.get<Breadcrumb[]>(RestService.REST_URL + 'file-system/breadcrumbs', {
      params: new ParamsBuilder()
        .addFileSystemParams(type, path)
        .build()
    });
  }

  deleteFile(type: FileSystemType, path?: string): Observable<FileDeleteResult> {
    return this.http.delete<FileDeleteResult>(RestService.REST_URL + 'file-system/delete', {
      params: new ParamsBuilder()
        .addFileSystemParams(type, path)
        .build()
    });
  }

  getFileViewUrl(type: FileSystemType, path?: string): string {
    return RestService.VIEW_URL + RestService.getFileUrlParams(type, path);
  }

  getFileDownloadUrl(type: FileSystemType, path?: string): string {
    return RestService.DOWNLOAD_URL + RestService.getFileUrlParams(type, path);
  }
}
