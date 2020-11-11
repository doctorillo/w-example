export interface ActiveCmpFn {
  search(x: string): void;

  fetch(): void;

  toHole(id: string): void;
}