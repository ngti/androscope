import {Component, OnDestroy, OnInit} from '@angular/core';
import {QueryModelService} from '../../common/query-model/query-model.service';
import {ActivatedRoute} from '@angular/router';
import {Uri} from '../../common/query-model/uri';

@Component({
  selector: 'app-provider-data',
  templateUrl: './provider-data.component.html',
  styleUrls: ['./provider-data.component.css']
})
export class ProviderDataComponent {

  constructor(
    private model: QueryModelService,
    route: ActivatedRoute
  ) {
    route.url.subscribe(newUrl => {
      const uriString = decodeURIComponent(route.snapshot.params.uri);
      model.uri = new Uri(uriString);
    });
  }

}
