import {Component, OnInit} from '@angular/core';
import {QueryModelService} from '../common/query-model/query-model.service';
import {Uri} from '../common/query-model/uri';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-provider',
  templateUrl: './provider.component.html',
  styleUrls: ['./provider.component.css']
})
export class ProviderComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public model: QueryModelService
  ) {
    console.log('ProviderComponent created');

    // route.params.subscribe(newUrl => {
    //   console.log('newUrl: ' + newUrl);
    //
    //   let uriContent = route.snapshot.params.uri;
    //   console.log('uriContent: ' + uriContent);
    //   if (uriContent != null) {
    //     uriContent = decodeURIComponent(uriContent);
    //   } else {
    //     uriContent = '';
    //   }
    //   model.uri = new Uri(uriContent);
    // });
  }

  ngOnInit() {
  }

  submitUri(newUri: Uri) {
    console.log('submitUri ' + newUri.content);
    this.router.navigate([encodeURIComponent(newUri.content.trim())], {relativeTo: this.route});
//    this.uri = newUri;
//    this.recreateDataSource();
//    this.updateDataSource();
  }

}
