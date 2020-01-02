import {Component, OnDestroy, OnInit} from '@angular/core';
import {QueryModelService} from '../common/query-model/query-model.service';
import {Uri} from '../common/query-model/uri';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-provider',
  templateUrl: './provider.component.html',
  styleUrls: ['./provider.component.css']
})
export class ProviderComponent implements OnInit, OnDestroy {

  uri: Uri;

  private uriSubscription: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private model: QueryModelService
  ) {
    console.log('ProviderComponent created');
  }

  ngOnInit() {
    this.uriSubscription = this.model.uriObserver.subscribe(newUri => this.uri = Object.assign(new Uri(), newUri));
  }

  ngOnDestroy(): void {
    this.uriSubscription.unsubscribe();
  }

  submitUri(newUri: Uri) {
    console.log('submitUri ' + newUri.content);
    this.router.navigate([encodeURIComponent(newUri.content.trim())], {relativeTo: this.route});
  }

}
