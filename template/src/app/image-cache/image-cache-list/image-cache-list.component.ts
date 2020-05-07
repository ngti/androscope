import { Component, OnInit } from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {RestService} from '../../common/rest/rest.service';
import {ImageCache} from '../../common/rest/image-cache-data';

@Component({
  selector: 'app-image-cache-list',
  templateUrl: './image-cache-list.component.html',
  styleUrls: ['./image-cache-list.component.css']
})
export class ImageCacheListComponent implements OnInit {

  private imageCachesSubject = new BehaviorSubject<ImageCache[]>([]);
  imageCaches$ = this.imageCachesSubject.asObservable();

  private loadingSubject = new BehaviorSubject<boolean>(true);
  loading$ = this.loadingSubject.asObservable();

  constructor(private restService: RestService) {
  }

  ngOnInit(): void {
    this.restService.getImageCacheList().subscribe(list => {
      this.imageCachesSubject.next(list);
      this.loadingSubject.next(false);
    });
  }

  getImageCachePath(imageCache: ImageCache): string {
    return './image-cache/' + encodeURIComponent(imageCache.type);
  }

}
