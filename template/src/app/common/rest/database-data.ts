export class Database {
  name: string;
  title: string;
  description?: string;
  error?: string;
}

export class DatabaseInfo {
  valid: boolean;
  errorMessage: string;
  fullPath: string;
  size: string;
  tables: string[];
  views: string[];
  triggers: string[];
  indexes: string[];
}

export class ExecuteSqlResult {
  success: boolean;
  message: string;
}

export class SqlParams {
  constructor(
    readonly sql: string
  ) {
  }

}
