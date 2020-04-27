export class Database {
  name: string;
  title: string;
  description?: string;
  error?: string;
}

export class DatabaseInfo {
  name: string;
  size: string;
  tables: string[];
  views: string[];
  triggers: string[];
  indexes: string[];
}
