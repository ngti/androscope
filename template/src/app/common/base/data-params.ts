import {SortDirection} from '@angular/material/sort';

export class DataParams {

  pageSize: number;
  pageNumber = 0;
  sortOrder: SortDirection = '';
  sortColumn?: string = null;
  readonly timestamp: number = Date.now();

  private changed = true;

  hasSorting(): boolean {
    return this.sortColumn != null && this.sortColumn.length > 0 && this.sortOrder.length > 0;
  }

  updatePagination(pageSize: number, pageNumber: number) {
    if (this.pageSize !== pageSize || this.pageNumber !== pageNumber) {
      this.pageSize = pageSize;
      this.pageNumber = pageNumber;
      this.changed = true;
    }
  }

  updateSorting(sortOrder: SortDirection = '', sortColumn?: string) {
    if (this.sortOrder !== sortOrder || this.sortColumn !== sortColumn) {
      this.sortOrder = sortOrder;
      this.sortColumn = sortColumn;
      this.changed = true;
    }
  }

  consume(block: (DataParams) => void) {
    if (this.changed) {
      block(this);
      this.changed = false;
    }
  }

}
