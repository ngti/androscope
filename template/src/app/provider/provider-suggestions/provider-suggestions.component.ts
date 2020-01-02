import { Component, OnInit } from '@angular/core';
import {QueryModelService} from '../../common/query-model/query-model.service';
import {Uri} from '../../common/query-model/uri';

@Component({
  selector: 'app-provider-suggestions',
  templateUrl: './provider-suggestions.component.html',
  styleUrls: ['./provider-suggestions.component.css']
})
export class ProviderSuggestionsComponent implements OnInit {

  constructor(private model: QueryModelService) {
    model.uri = new Uri();
  }

  ngOnInit() {
  }

}
