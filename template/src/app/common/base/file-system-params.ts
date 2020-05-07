import {FileSystemType} from '../rest/rest.service';

export class FileSystemParams {

  constructor(
    public readonly fileSystemType: FileSystemType,
    public readonly path?: string,
    private timestampInternal: number = Date.now()
  ) {
  }

  private static concatPaths(parent: string, path: string): string {
    if (parent == null || parent.length === 0) {
      return path;
    }
    if (path == null || path.length === 0) {
      return parent;
    }
    return parent + '/' + path;
  }

  hasPath(): boolean {
    return this.path != null && this.path.length > 0;
  }

  appendPath(path: string): string {
    return FileSystemParams.concatPaths(this.path, path);
  }

  withAppendedPath(path: string): FileSystemParams {
    return new FileSystemParams(
      this.fileSystemType,
      this.appendPath(path),
      this.timestampInternal
    );
  }

  get timestamp(): number {
    return this.timestampInternal;
  }

  updateTimestamp() {
    this.timestampInternal = Date.now();
  }

  get encodedPath(): string {
    return encodeURIComponent(this.path);
  }

  equals(other: FileSystemParams): boolean {
    return this.fileSystemType === other.fileSystemType
      && this.path === other.path;
  }
}
