export class Database {
  name: string;
  title: string;
  description?: string;
  error?: string;
}

export class DatabaseInfo {
  fullPath: string;
  size: string;
  tables: string[];
  views: string[];
  triggers: string[];
  indexes: string[];
}
