import {Component, OnDestroy, OnInit} from '@angular/core';
import {Uri} from '../common/query-model/uri';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {ProviderModelService} from './model/provider-model.service';

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
    private model: ProviderModelService
  ) {
  }

  ngOnInit() {
    this.uriSubscription = this.model.uri$.subscribe(
      newUri => this.uri = Object.assign(new Uri(), newUri));
  }

  ngOnDestroy(): void {
    this.uriSubscription.unsubscribe();
  }

  submitUri(newUri: Uri) {
    this.router.navigate([newUri.contentUrlEncoded], {relativeTo: this.route});
  }

}
