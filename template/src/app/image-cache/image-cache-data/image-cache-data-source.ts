import {BaseDataSource} from '../../common/base/base-data-source';
import {ImageCacheEntry, ImageCacheInfo} from '../../common/rest/image-cache-data';
import {DataParams} from '../../common/base/data-params';
import {BehaviorSubject, Observable} from 'rxjs';
import {RestService} from '../../common/rest/rest.service';

export class ImageCacheDataSource extends BaseDataSource<ImageCacheEntry> {

  static readonly DEFAULT_PAGE_SIZE = 100;

  data = this.connect();

  private timestamp: number = Date.now();

  constructor(
    private restService: RestService,
    readonly imageCacheType: string,
    loadingSubject: BehaviorSubject<boolean>,
    private infoSubject: BehaviorSubject<ImageCacheInfo>
  ) {
    super(ImageCacheDataSource.DEFAULT_PAGE_SIZE, loadingSubject);

    restService.getImageCacheInfo(imageCacheType).subscribe(info => {
      this.infoSubject.next(info);
    });
  }

  protected onGenerateNetworkRequest(dataParams: DataParams): Observable<ImageCacheEntry[]> {
    return this.restService.getImageCacheData(this.imageCacheType, this.timestamp, dataParams);
  }
}
