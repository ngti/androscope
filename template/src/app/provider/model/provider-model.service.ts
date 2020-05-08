import {Injectable} from '@angular/core';
import {QueryModelService} from '../../common/query-model/query-model.service';
import {Uri} from '../../common/query-model/uri';

@Injectable({
  providedIn: 'root'
})
export class ProviderModelService extends QueryModelService<Uri> {

  constructor() {
    super(new Uri());
  }
}
