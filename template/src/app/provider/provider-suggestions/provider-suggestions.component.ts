import {Component} from '@angular/core';
import {Uri} from '../../common/query-model/uri';
import {ProviderModelService} from '../model/provider-model.service';

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
      'Queries the state of the media scanner',
      'scanner'
    ),
  ];

  constructor(private model: ProviderModelService) {
    // Clear input field
    model.uri = new Uri();
  }

}
