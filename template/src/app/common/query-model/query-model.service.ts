import {BehaviorSubject, Observable} from 'rxjs';
import {Uri} from './uri';

export abstract class QueryModelService<U extends Uri> {

  private readonly uriSubject: BehaviorSubject<U>;
  readonly uri$: Observable<U>;

  protected constructor(initialUri: U) {
    this.uriSubject = new BehaviorSubject<U>(initialUri);
    this.uri$ = this.uriSubject.asObservable();
  }

  get uri(): U {
    return this.uriSubject.value;
  }

  set uri(newUri: U) {
    this.uriSubject.next(newUri);
  }
}
