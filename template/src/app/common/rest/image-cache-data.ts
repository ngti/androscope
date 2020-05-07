export class ImageCache {
  type: string;
  title: string;
}

export class ImageCacheInfo {
  totalEntries: number;
  error?: string = null;
}

export class ImageCacheEntry {
  fileName: string;
  size: number;
}
