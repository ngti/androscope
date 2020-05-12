import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Uri} from '../../common/query-model/uri';
import {ProviderModelService} from '../model/provider-model.service';

@Component({
  selector: 'app-provider-data',
  templateUrl: './provider-data.component.html',
  styleUrls: ['./provider-data.component.css']
})
export class ProviderDataComponent {

  constructor(
    readonly model: ProviderModelService,
    route: ActivatedRoute
  ) {
    route.url.subscribe(() => {
      const uriString = decodeURIComponent(route.snapshot.params.uri);
      model.uri = new Uri(uriString);
    });
  }

}
