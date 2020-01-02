import { Injectable } from '@angular/core';
import {Uri} from './uri';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class QueryModelService {

  private uri$: BehaviorSubject<Uri> = new BehaviorSubject<Uri>(new Uri(''));

  uriObserver = this.uri$.asObservable();

  constructor() {
    console.log('QueryModelService created');
  }

  get uri(): Uri {
    return this.uri$.value;
  }

  set uri(newUri: Uri) {
    console.log('Set uri: ' + newUri.content);
    this.uri$.next(newUri);
  }
}
