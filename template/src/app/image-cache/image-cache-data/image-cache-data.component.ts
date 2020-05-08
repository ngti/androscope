import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {BehaviorSubject} from 'rxjs';
import {ImageCacheEntry, ImageCacheInfo} from '../../common/rest/image-cache-data';
import {RestService} from '../../common/rest/rest.service';
import {ActivatedRoute} from '@angular/router';
import {ImageCacheDataSource} from './image-cache-data-source';

@Component({
  selector: 'app-image-cache-data',
  templateUrl: './image-cache-data.component.html',
  styleUrls: ['./image-cache-data.component.css']
})
export class ImageCacheDataComponent implements OnInit, AfterViewInit {

  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;

  readonly defaultPageSize = ImageCacheDataSource.DEFAULT_PAGE_SIZE;

  dataSource: ImageCacheDataSource;

  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  private infoSubject = new BehaviorSubject<ImageCacheInfo>(new ImageCacheInfo());
  info$ = this.infoSubject.asObservable();

  constructor(
    private restService: RestService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit() {
    this.route.url.subscribe(() => {
      const imageCacheType = decodeURIComponent(this.route.snapshot.params.type);

      if (this.paginator != null) {
        this.paginator.pageIndex = 0;
      }

      this.dataSource = new ImageCacheDataSource(
        this.restService,
        imageCacheType,
        this.loadingSubject,
        this.infoSubject
      );
      this.dataSource.updatePagination(this.paginator);
      this.dataSource.reloadDataIfNeeded();
    });
  }

  ngAfterViewInit(): void {
    this.paginator.page.subscribe(() => {
      this.dataSource.updatePagination(this.paginator);
      this.dataSource.reloadDataIfNeeded();
    })
  }

  getThumbnailUri(entry: ImageCacheEntry): string {
    return this.restService.getImageCacheThumbnailUrl(this.dataSource.imageCacheType, entry.fileName)
  }
}
