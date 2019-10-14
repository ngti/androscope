export class Uri {

  constructor(public content: string) {}

  isEmpty(): boolean {
    return this.content.trim().length === 0;
  }
}
