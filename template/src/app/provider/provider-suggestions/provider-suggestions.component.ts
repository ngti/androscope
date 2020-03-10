import {Component} from '@angular/core';
import {QueryModelService} from '../../common/query-model/query-model.service';
import {Uri} from '../../common/query-model/uri';

export class UriSuggestion {
  constructor(
    public uri: string,
    public description: string,
    public icon: string
  ) {
  }

  uriForUrl(): string {
    return encodeURIComponent(this.uri);
  }
}

@Component({
  selector: 'app-provider-suggestions',
  templateUrl: './provider-suggestions.component.html',
  styleUrls: ['./provider-suggestions.component.css']
})
export class ProviderSuggestionsComponent {

  suggestions: UriSuggestion[] = [
    new UriSuggestion(
      'content://media/external/file',
      'Displays the content of phone\'s storage, requires READ_EXTERNAL_STORAGE permission',
      'folder'
    ),
    new UriSuggestion(
      'content://com.android.contacts/contacts',
      'Displays phone contacts, requires READ_CONTACTS permission',
      'contacts'
    ),
    new UriSuggestion(
      'content://media/none/media_scanner',
      'Displays phone contacts, requires READ_CONTACTS permission',
      'scanner'
    ),
  ];

  constructor(private model: QueryModelService) {
    // Clear input field
    model.uri = new Uri();
  }

}
