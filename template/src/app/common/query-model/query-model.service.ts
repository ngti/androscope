import { Injectable } from '@angular/core';
import {Uri} from './uri';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class QueryModelService {

  private uriSubject: BehaviorSubject<Uri> = new BehaviorSubject<Uri>(new Uri());

  uri$ = this.uriSubject.asObservable();

  constructor() {
    console.log('QueryModelService created');
  }

  get uri(): Uri {
    return this.uriSubject.value;
  }

  set uri(newUri: Uri) {
    console.log('Set uri: ' + newUri.content);
    this.uriSubject.next(newUri);
  }
}
