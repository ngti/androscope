import {Uri} from '../../common/query-model/uri';

export class DatabaseUri extends Uri {

  constructor(
    databaseName: string,
    databaseQuery: string = null
  ) {
    super(DatabaseUri.buildUri(databaseName, databaseQuery));
  }

  private static buildUri(
    databaseName: string,
    databaseQuery: string
  ) {
    let result = 'database://' + encodeURIComponent(databaseName);
    if (databaseQuery != null && databaseQuery.length > 0) {
      result += '?query=' + encodeURIComponent(databaseQuery);
    }
    return result;
  }
}
