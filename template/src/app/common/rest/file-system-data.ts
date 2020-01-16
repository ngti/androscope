export class Breadcrumb {
  name: string;
  path: string;
}

export class FileSystemEntry {
  name: string;
  extension?: string;
  isFolder: boolean;
  date: string;
  size: string;
}

export class FileSystemCount {
  totalEntries: number;
}

export class FileDeleteResult {
  success: boolean;
  errorMessage?: string;
}
