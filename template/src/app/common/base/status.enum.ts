export enum Status {
  UNDEFINED,
  IN_PROGRESS,
  SUCCESS,
  ERROR
}

export class StatusData {

  constructor(
    private readonly status: Status = Status.UNDEFINED,
    readonly message: string = null
  ) {
  }

  get display(): boolean {
    return this.status !== Status.UNDEFINED;
  }

  get isInProgress(): boolean {
    return this.status === Status.IN_PROGRESS;
  }

  get isError(): boolean {
    return this.status === Status.ERROR;
  }

  get alertClass(): string {
    switch (this.status) {
      case Status.IN_PROGRESS:
        return 'alert-primary';
      case Status.SUCCESS:
        return 'alert-success';
      case Status.ERROR:
        return 'alert-danger';
      case Status.UNDEFINED:
        return null;
    }
  }
}
