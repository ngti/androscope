export class Uri {

  content: string;
  timestamp: number = Date.now();

  constructor(content: string = '') {
    this.content = content.trim();
  }

  get contentUrlEncoded(): string {
    return encodeURIComponent(this.content);
  }

  isEmpty(): boolean {
    return this.content.length === 0;
  }
}
