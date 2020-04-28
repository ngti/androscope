import {Uri} from '../../common/query-model/uri';

export class DatabaseUri extends Uri {

  constructor(
    databaseName: string,
    databaseQueryKey: string = null,
    databaseQueryValue: string = null
  ) {
    super(DatabaseUri.buildUri(databaseName, databaseQueryKey, databaseQueryValue));
  }

  private static buildUri(
    databaseName: string,
    databaseQueryKey: string,
    databaseQueryValue: string
  ) {
    let result = 'database://' + encodeURIComponent(databaseName);
    if (databaseQueryKey != null && databaseQueryKey.length > 0) {
      result += `?${databaseQueryKey}=` + encodeURIComponent(databaseQueryValue);
    }
    return result;
  }
}
