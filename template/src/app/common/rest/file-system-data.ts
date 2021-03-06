export class FileSystemEntry {
  name: string;
  extension: string;
  isFolder: boolean;
  date: string;
  size: string;

  static getFullName(entry: FileSystemEntry): string {
    let name = entry.name;
    if (entry.extension.length > 0) {
      name += '.' + entry.extension;
    }
    return name;
  }
}

export class FileSystemCount {
  totalEntries: number;
}

export class Breadcrumb {
  name: string;
  path: string;
}
