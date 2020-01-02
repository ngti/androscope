import {Component, OnDestroy, OnInit} from '@angular/core';
import {QueryModelService} from '../../common/query-model/query-model.service';
import {ActivatedRoute} from '@angular/router';
import {Uri} from '../../common/query-model/uri';

@Component({
  selector: 'app-provider-data',
  templateUrl: './provider-data.component.html',
  styleUrls: ['./provider-data.component.css']
})
export class ProviderDataComponent implements OnInit, OnDestroy {

  constructor(
    private model: QueryModelService,
    private route: ActivatedRoute
  ) {
    console.log('ProviderDataComponent created');

    route.url.subscribe(newUrl => {
      console.log('newUrl: ' + newUrl);

      let uriContent = route.snapshot.params.uri;
      console.log('uriContent: ' + uriContent);
      if (uriContent != null) {
        uriContent = decodeURIComponent(uriContent);
      } else {
        uriContent = '';
      }
      model.uri = new Uri(uriContent);
    });
  }

  ngOnInit() {
    console.log('ProviderDataComponent - onInit');
  }

  ngOnDestroy(): void {
    console.log('ProviderDataComponent - onDestroy');
  }

}
