export class Breadcrumb {
  name: string;
  path: string;
}

export class FileSystemEntry {
  name: string;
  extension?: string;
  isFolder: boolean;
  date: Date;
  size: string;
  hovered?: boolean;
}

export class FileSystemCount {
  totalEntries: number;
}
