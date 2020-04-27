export class Uri {

  readonly content: string;
  timestamp: number = Date.now();

  constructor(content: string = '') {
    this.content = content.trim();
  }

  isEmpty(): boolean {
    return this.content.length === 0;
  }

  get contentUrlEncoded(): string {
    return encodeURIComponent(this.content);
  }
}
